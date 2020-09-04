package com.example.minichat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.minichat.data.Chat;
import com.example.minichat.data.Friend;
import com.example.minichat.data.User;
import com.example.minichat.dataMenagement.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final int FRIENDS_CODE = 253;
    private MainViewModel model;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    NavController navController;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        model = new ViewModelProvider(this).get(MainViewModel.class);
        user = (User) getIntent().getSerializableExtra("user");
        model.createConnectionsAndListenersForDatabase(user);
    }

    public MainViewModel getModel() {
        return model;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            if (requestCode == MainActivity.FRIENDS_CODE) {
                //Обрабатываем возвращенные данные из Friend Activity 
                assert data != null;
                Friend friend = (Friend) data.getSerializableExtra("friend");
                assert friend != null;
                if(friend.getUsername().equals(user.getUsername())){
                    Toast.makeText(MainActivity.this, "Нельзя отправить запрос самому себе", Toast.LENGTH_LONG).show();
                    return;
                }
                navController.navigate(R.id.navigation_home);
                database.getReference("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String key = user.getUsername()+"_"+friend.getUsername();
                        if(!snapshot.hasChild(key) && !snapshot.hasChild(friend.getUsername()+"_"+user.getUsername())){
                            Chat chat = new Chat(key, friend.getUsername() + " и " + user.getUsername(),
                                Arrays.asList(friend.getUsername(), user.getUsername()));

                            snapshot.child(key).getRef().setValue(chat);
                            user.getReference().child(Chat.CHAT_REFERENCE_PATH).push().setValue(key);
                            friend.getReference().child(Chat.CHAT_REFERENCE_PATH).push().setValue(key);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
    }
}
