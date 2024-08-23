package com.mecha.app.ui.profile;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileViewModel extends AndroidViewModel {

    private MutableLiveData<UserProfile> profile;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profile = new MutableLiveData<>();
        loadProfile();
    }

    public LiveData<UserProfile> getProfile() {
        return profile;
    }

    private void loadProfile() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    profile.setValue(userProfile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибок
                }
            });
        } else {
            // Обработка случая, когда пользователь не авторизован
            // Например, можно установить profile как null или создать лог для отладки
            profile.setValue(null);
        }
    }

    public void saveProfile(Uri avatarUri, String carManufacturer, String carModel, String bodyType, String carYear, String vinNumber, String nickName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Сохранение аватара в Firebase Storage и получение URL
            StorageReference avatarRef = FirebaseStorage.getInstance().getReference("avatars").child(userId + ".jpg");
            avatarRef.putFile(avatarUri).addOnSuccessListener(taskSnapshot -> avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String avatarUrl = uri.toString();

                UserProfile profile = new UserProfile(avatarUrl, carManufacturer, carModel, bodyType, carYear, vinNumber, nickName);
                reference.setValue(profile).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Обновление шапки профиля сразу после сохранения данных
                        Intent intent = new Intent("com.mecha.app.UPDATE_PROFILE_HEADER");
                        intent.putExtra("avatarUrl", avatarUrl);
                        intent.putExtra("nickName", nickName);
                        getApplication().sendBroadcast(intent);
                        Toast.makeText(getApplication(), "Данные сохранены", Toast.LENGTH_SHORT).show();
                    }
                });
            }));
        } else {
            // Обработка случая, когда пользователь не авторизован
            // Например, можно создать лог для отладки
        }
    }
}
