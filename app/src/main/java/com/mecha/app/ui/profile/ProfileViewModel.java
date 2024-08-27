package com.mecha.app.ui.profile;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final MutableLiveData<UserProfile> profile;
    private final RequestQueue requestQueue;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profile = new MutableLiveData<>();
        requestQueue = Volley.newRequestQueue(application);
    }

    public LiveData<UserProfile> getProfile() {
        return profile;
    }

    public void checkProfileExists() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String url = "http://" + "192.168.195.61" + ":3000/api/profile/" + userId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            String name = response.getString("name");
                            String avatarUrl = response.getString("avatarUrl");

                            // Получение списка карточек автомобилей
                            JSONArray carCardsArray = response.getJSONArray("carCards");
                            List<CarCard> carCards = new ArrayList<>();
                            for (int i = 0; i < carCardsArray.length(); i++) {
                                JSONObject cardObject = carCardsArray.getJSONObject(i);
                                CarCard card = new CarCard(
                                        cardObject.getString("carImageUrl"),
                                        cardObject.getString("carName"),
                                        cardObject.getString("carModel"),
                                        cardObject.getString("carYear"),
                                        cardObject.getString("vinNumber"),
                                        cardObject.getString("engine"),
                                        cardObject.getString("transmission")
                                );
                                carCards.add(card);
                            }

                            UserProfile userProfile = new UserProfile(avatarUrl, name, carCards);
                            profile.setValue(userProfile);
                        } catch (JSONException e) {
                            Log.e("ProfileViewModel", "JSON Parsing error", e);
                        }
                    },
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            profile.setValue(null);
                        } else {
                            Toast.makeText(getApplication(), "Ошибка при проверке профиля", Toast.LENGTH_SHORT).show();
                        }
                    });

            requestQueue.add(jsonObjectRequest);
        } else {
            profile.setValue(null);
        }
    }

    public void saveProfile(Uri avatarUri, String nickName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            if (avatarUri != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("avatars/" + userId + ".jpg");

                storageRef.putFile(avatarUri)
                        .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String avatarUrl = downloadUri.toString();
                            saveProfileData(userId, avatarUrl, nickName);
                        }))
                        .addOnFailureListener(e -> Toast.makeText(getApplication(), "Ошибка загрузки аватара", Toast.LENGTH_SHORT).show());
            } else {
                saveProfileData(userId, null, nickName);
            }
        }
    }

    private void saveProfileData(String userId, @Nullable String avatarUrl, String nickName) {
        String url = "http://192.168.195.61:3000/api/profile/create";

        JSONObject profileData = new JSONObject();
        try {
            profileData.put("userId", userId);
            profileData.put("avatarUrl", avatarUrl);
            profileData.put("nickName", nickName);

            JSONArray carCardsArray = new JSONArray();
            UserProfile currentProfile = profile.getValue();
            if (currentProfile != null) {
                for (CarCard card : currentProfile.getCarCards()) {
                    JSONObject cardObject = new JSONObject();
                    cardObject.put("carImageUrl", card.getCarImageUrl());
                    cardObject.put("carName", card.getCarName());
                    cardObject.put("carModel", card.getCarModel());
                    cardObject.put("carYear", card.getCarYear());
                    cardObject.put("vinNumber", card.getVinNumber());
                    cardObject.put("engine", card.getEngine());
                    cardObject.put("transmission", card.getTransmission());
                    carCardsArray.put(cardObject);
                }
            }

            profileData.put("carCards", carCardsArray);

        } catch (JSONException e) {
            Log.e("ProfileViewModel", "JSON Creation error", e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileData,
                response -> Toast.makeText(getApplication(), "Профиль успешно сохранен", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(getApplication(), "Ошибка при сохранении профиля", Toast.LENGTH_SHORT).show());

        requestQueue.add(jsonObjectRequest);
    }

    public void addNewCarCard() {
        // Логика добавления новой карточки автомобиля
        UserProfile currentProfile = profile.getValue();
        if (currentProfile != null) {
            List<CarCard> carCards = currentProfile.getCarCards();
            CarCard newCard = new CarCard("defaultImageUrl", "New Car", "Model", "Year", "VIN", "Engine", "Transmission");
            carCards.add(newCard);
            profile.setValue(currentProfile);
        }
    }

    public void editCarCard() {
        // Логика редактирования выбранной карточки автомобиля
        // Возможно, потребуется передать идентификатор или позицию карточки для редактирования
    }

    public void deleteCarCard() {
        // Логика удаления выбранной карточки автомобиля
        UserProfile currentProfile = profile.getValue();
        if (currentProfile != null) {
            List<CarCard> carCards = currentProfile.getCarCards();
            if (!carCards.isEmpty()) {
                carCards.remove(carCards.size() - 1); // Удаление последней карточки как пример
                profile.setValue(currentProfile);
            }
        }
    }
}
