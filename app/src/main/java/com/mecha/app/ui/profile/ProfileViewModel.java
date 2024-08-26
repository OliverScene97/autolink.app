package com.mecha.app.ui.profile;

import android.app.Application;
import android.net.Uri;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileViewModel extends AndroidViewModel {

    private MutableLiveData<UserProfile> profile;
    private RequestQueue requestQueue; // Для отправки запросов через Volley

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profile = new MutableLiveData<>();
        requestQueue = Volley.newRequestQueue(application); // Инициализация RequestQueue
    }

    public LiveData<UserProfile> getProfile() {
        return profile;
    }

    // Метод для проверки существования профиля
    public void checkProfileExists() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String url = "http://" + "192.168.195.61" + ":3000/api/profile/" + userId;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // Если профиль существует, устанавливаем данные профиля
                        try {
                            String name = response.getString("name");
                            String carManufacturer = response.getString("carManufacturer");
                            String carModel = response.getString("carModel");
                            String bodyType = response.getString("bodyType");
                            String carYear = response.getString("carYear");
                            String vinNumber = response.getString("vinNumber");
                            String avatarUrl = response.getString("avatarUrl");

                            UserProfile userProfile = new UserProfile(avatarUrl, carManufacturer, carModel, bodyType, carYear, vinNumber, name);
                            profile.setValue(userProfile);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            // Профиль не найден, можно предложить создать новый
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

    public void saveProfile(Uri avatarUri, String carManufacturer, String carModel, String bodyType, String carYear, String vinNumber, String nickName) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String url = "http://192.168.195.61:3000/api/profile/create";

            // Создание JSON объекта с данными профиля
            JSONObject profileData = new JSONObject();
            try {
                profileData.put("userId", userId);
                profileData.put("carManufacturer", carManufacturer);
                profileData.put("carModel", carModel);
                profileData.put("bodyType", bodyType);
                profileData.put("carYear", carYear);
                profileData.put("vinNumber", vinNumber);
                profileData.put("nickName", nickName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Создание POST-запроса для сохранения профиля
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileData,
                    response -> {
                        Toast.makeText(getApplication(), "Профиль успешно сохранен", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        Toast.makeText(getApplication(), "Ошибка при сохранении профиля", Toast.LENGTH_SHORT).show();
                    });

            requestQueue.add(jsonObjectRequest);
        }
    }
}
