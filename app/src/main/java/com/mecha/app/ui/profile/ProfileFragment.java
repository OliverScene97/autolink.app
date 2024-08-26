package com.mecha.app.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.mecha.app.LoginActivity;
import com.mecha.app.R;
import com.squareup.picasso.Picasso;
import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView avatarImageView;
    private EditText carManufacturerEditText, carModelEditText, carYearEditText, vinNumberEditText, nickNameEditText;
    private Spinner bodyTypeSpinner;
    private Button saveProfileButton;
    private Button logoutButton;
    private ProfileViewModel profileViewModel;
    private Uri avatarUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        avatarImageView = view.findViewById(R.id.avatarImageView);
        carManufacturerEditText = view.findViewById(R.id.carManufacturerEditText);
        carModelEditText = view.findViewById(R.id.carModelEditText);
        carYearEditText = view.findViewById(R.id.carYearEditText);
        vinNumberEditText = view.findViewById(R.id.vinNumberEditText);
        nickNameEditText = view.findViewById(R.id.nickNameEditText);
        bodyTypeSpinner = view.findViewById(R.id.bodyTypeSpinner);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.body_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyTypeSpinner.setAdapter(adapter);

        avatarImageView.setOnClickListener(v -> openFileChooser());

        saveProfileButton.setOnClickListener(v -> saveProfile());

        logoutButton.setOnClickListener(v -> logout());

        // Проверка существования профиля
        profileViewModel.checkProfileExists();

        profileViewModel.getProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                carManufacturerEditText.setText(profile.carManufacturer);
                carModelEditText.setText(profile.carModel);
                carYearEditText.setText(profile.carYear);
                vinNumberEditText.setText(profile.vinNumber);
                nickNameEditText.setText(profile.nickName);

                ArrayAdapter<CharSequence> bodyTypeAdapter = (ArrayAdapter<CharSequence>) bodyTypeSpinner.getAdapter();
                if (bodyTypeAdapter != null) {
                    int position = bodyTypeAdapter.getPosition(profile.bodyType);
                    bodyTypeSpinner.setSelection(position);
                }

                Picasso.get().load(profile.avatarUrl).into(avatarImageView);
            } else {
                // Профиль не существует, предложить создать новый
                Toast.makeText(getActivity(), "Профиль не найден. Пожалуйста, создайте новый.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            Picasso.get().load(avatarUri).into(avatarImageView);
        }
    }

    private void saveProfile() {
        String carManufacturer = carManufacturerEditText.getText().toString().trim();
        String carModel = carModelEditText.getText().toString().trim();
        String carYear = carYearEditText.getText().toString().trim();
        String vinNumber = vinNumberEditText.getText().toString().trim();
        String bodyType = bodyTypeSpinner.getSelectedItem().toString();
        String nickName = nickNameEditText.getText().toString().trim();

        if (carManufacturer.isEmpty() || carModel.isEmpty() || carYear.isEmpty() || vinNumber.isEmpty() || avatarUri == null || nickName.isEmpty()) {
            Toast.makeText(getActivity(), "Пожалуйста, заполните все поля и выберите аватар", Toast.LENGTH_SHORT).show();
            return;
        }

        profileViewModel.saveProfile(avatarUri, carManufacturer, carModel, bodyType, carYear, vinNumber, nickName);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}
