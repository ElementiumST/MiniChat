package com.example.minichat.preMain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private static final int AUTH_CODE = 8394;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences("AppData", MODE_PRIVATE);

        //Если активити вызвана с намерением выхода из профиля
        if(getIntent().hasExtra("signOut")){
            boolean signOut = getIntent().getBooleanExtra("signOut", false);
            if(signOut){
                //Удаляем ссылку на существующий профиль
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("userID");
                editor.apply();
            }
        }

        //Если в кеше есть данные о прошлом профиле, загружаем его
        if(preferences.contains("userID")){
            String userID = preferences.getString("userID", null);
            if(userID == null)
                startAuth();
            else {
                loadUserInstance(userID);
            }
        } else {
            startAuth();
        }
    }

    /**
     * Подгружает из бд обьект пользователя
     * @param userID id пользователя
     */
    private void loadUserInstance(String userID) {
        database.getReference("users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                finishUpload(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * вызывается по завершению подгрузки данных о пользователе
     * @param user Обьект пользователя
     */
    private void finishUpload(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK)
            switch (requestCode){
                case AUTH_CODE:
                    // Если аунтентификация завершена успешно, получаем от неё обьект пользователя и помещаем его id в кеш
                    if (data == null) return;
                    User user = (User) data.getSerializableExtra("user");
                    if(user == null) return;

                    preferences.edit().putString("userID", Utils.HashMD5(user.getUsername())).apply();
                    finishUpload(user);
                    break;

            }
        super.onActivityResult(requestCode, resultCode, data);

    }
    // Запускает активити Авторизации
    private void startAuth() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivityForResult(intent, AUTH_CODE);
    }
}
