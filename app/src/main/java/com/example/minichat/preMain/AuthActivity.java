package com.example.minichat.preMain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    TextView username, password;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button = findViewById(R.id.sign);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setTextColor(Color.BLACK);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        database.getReference(User.USER_REFERENCE_PATH).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String md5username = Utils.HashMD5(username.getText().toString());
                String clientPassword = password.getText().toString();
                if(md5username == null){
                    Toast.makeText(AuthActivity.this, "Некорректно введено имя пользователя", Toast.LENGTH_LONG).show();
                    username.setTextColor(Color.RED);
                }
                assert md5username != null;
                if(snapshot.hasChild(md5username)){
                    DataSnapshot userSnapshot = snapshot.child(md5username);
                    String serverPassword = userSnapshot.child("password").getValue(String.class);
                    if(!clientPassword.equals(serverPassword)) {
                        Toast.makeText(AuthActivity.this, "Неправильный пароль", Toast.LENGTH_LONG).show();
                        password.setTextColor(Color.RED);
                    } else {
                        User user = userSnapshot.getValue(User.class);
                        Toast.makeText(AuthActivity.this, "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                        finishAuth(user);
                    }
                }else{
                    User user = new User(username.getText().toString(), clientPassword);
                    snapshot.getRef().child(md5username).setValue(user);
                    Toast.makeText(AuthActivity.this, "успешная регистрация", Toast.LENGTH_LONG)
                            .show();
                    finishAuth(user);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void finishAuth(User user) {
        Intent intent = new Intent();
        intent.putExtra("user", user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
