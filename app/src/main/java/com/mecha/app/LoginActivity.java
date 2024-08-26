package com.mecha.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mecha.app.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("myApp", "Вход в функцию");
        // Проверка авторизации
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.w("myApp", "Первое условие");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Log.w("myApp", "Set content");

        setContentView(R.layout.activity_login);

        if (savedInstanceState == null) {
            Log.w("myApp", "Второе условие");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }

    public void onLoginSuccess() {
        Log.w("myApp", "Успех");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
