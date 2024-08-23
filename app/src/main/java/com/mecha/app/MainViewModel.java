package com.mecha.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainViewModel extends ViewModel {

    private DatabaseReference mDatabase;

    private MutableLiveData<String> someData;

    public MainViewModel() {
        // Инициализируем ссылку на базу данных
        mDatabase = FirebaseDatabase.getInstance("https://mecha-6b121-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        someData = new MutableLiveData<>();
        loadData();
    }

    public LiveData<String> getSomeData() {
        return someData;
    }

    private void loadData() {
        // Считываем данные из базы данных
        mDatabase.child("somePath").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String data = dataSnapshot.getValue(String.class);
                someData.setValue(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Обработка ошибки чтения данных
            }
        });
    }

    public void writeData(String data) {
        // Записываем данные в базу данных
        mDatabase.child("users").setValue(data);
    }
}
