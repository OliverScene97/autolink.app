package com.mecha.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mecha.app.ui.forum.ForumFragment;
import com.mecha.app.ui.help.HelpFragment;
import com.mecha.app.ui.home.HomeFragment;
import com.mecha.app.ui.master.MasterFragment;
import com.mecha.app.ui.profile.ProfileFragment;
import com.mecha.app.ui.profile.ProfileViewModel;
import com.mecha.app.ui.profile.UserProfile;
import com.mecha.app.ui.settings.SettingsFragment;
import com.mecha.app.ui.techsupport.TechSupportFragment;
import com.squareup.picasso.Picasso;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "mecha_prefs";
    private static final String KEY_MASTER_MODE = "isMasterMode";
    private ImageView avatarImageView;
    private TextView nicknameTextView;
    private ProfileViewModel profileViewModel;
    private Button switchModeButton;

    private boolean isMasterMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isMasterMode = sharedPreferences.getBoolean(KEY_MASTER_MODE, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        avatarImageView = headerView.findViewById(R.id.nav_header_avatar);
        nicknameTextView = headerView.findViewById(R.id.nav_header_nickname);

        // Инициализация кнопки переключения режима
        switchModeButton = navigationView.findViewById(R.id.button_switch_mode);
        if (switchModeButton == null) {
            Log.e("MainActivity", "Button switchModeButton not found in the layout.");
        } else {
            updateSwitchModeButtonText(); // Устанавливаем текст на кнопке при запуске
            switchModeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSwitchModeClicked();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        }

        profileViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(ProfileViewModel.class);
        loadUserProfile();

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                hideBottomNavigationView();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_bottom_help) {
                    selectedFragment = new HelpFragment();
                } else if (itemId == R.id.nav_bottom_service) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_bottom_chosen) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            if (isMasterMode) {
                loadMasterUI();
            } else {
                loadUserUI();
            }
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void updateSwitchModeButtonText() {
        if (switchModeButton != null) {
            if (isMasterMode) {
                switchModeButton.setText("Режим пользователя");
            } else {
                switchModeButton.setText("Режим мастера");
            }
        }
    }

    private void loadMasterUI() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MasterFragment())
                .commitAllowingStateLoss();
        updateMenuForMaster();
    }

    private void loadUserUI() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commitAllowingStateLoss();
        updateMenuForUser();
    }

    public void onSwitchModeClicked() {
        isMasterMode = !isMasterMode;
        sharedPreferences.edit().putBoolean(KEY_MASTER_MODE, isMasterMode).apply();

        updateSwitchModeButtonText();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateMenuForMaster() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu_master);
    }

    private void updateMenuForUser() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.drawer_menu_user);
    }

    public void updateProfileHeader(String avatarUrl, String nickname) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Picasso.get().load(avatarUrl).into(avatarImageView);
        }
        nicknameTextView.setText(nickname);
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        updateProfileHeader(userProfile.getAvatarUrl(), userProfile.getNickName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            updateProfileHeader(null, "Guest");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Fragment selectedFragment = null;
        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_forum) {
            selectedFragment = new ForumFragment();
        } else if (id == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (id == R.id.nav_tech_help) {
            selectedFragment = new TechSupportFragment();
        } else if (id == R.id.nav_switch_mode) {
            onSwitchModeClicked();
            return true;
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }
        return true;
    }

    public void showBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }

    public void hideBottomNavigationView() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }
}
