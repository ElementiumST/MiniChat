package com.example.minichat.friends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.data.Friend;
import com.example.minichat.data.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    User user;

    RecyclerView recyclerView;
    TextView notHasFriends;
    ProgressBar progressBar;
    FloatingActionButton addFriend;

    FriendsViewModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        bindViews();
        model = new ViewModelProvider(this).get(FriendsViewModel.class);
        user = (User) getIntent().getSerializableExtra("user");
        assert user != null;
        // Получение списка заявок и передача в модель
        user.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.INVISIBLE);
                if(!snapshot.hasChild(Friend.FRIEND_REFERENCE_PATH)) {
                    notHasFriends.setVisibility(View.VISIBLE);
                }else {
                    notHasFriends.setVisibility(View.INVISIBLE);
                }
                List<Friend> friendList = new ArrayList<>();
                for (DataSnapshot child:
                     snapshot.child(Friend.FRIEND_REFERENCE_PATH).getChildren()) {
                    friendList.add(child.getValue(Friend.class));
                }
                model.setFriends(friendList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FriendsAdapter adapter = new FriendsAdapter(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // добавление слушателя, который при клике создает диалог
        addFriend.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            EditText nameEditText = new EditText(this);
            builder.setTitle("Добавить друга")
                    .setView(nameEditText)
                    .setPositiveButton("Добавить", (dialogInterface, i) -> {
                        //Если пользователь найден, то мы отправляем ему заявку, иначе выводим тост
                        database.getReference(User.USER_REFERENCE_PATH)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String friendName = nameEditText.getText().toString();
                                String friendID = Objects.requireNonNull(Utils.HashMD5(friendName));
                                if(snapshot.hasChild(friendID)){
                                    //Если запрос уже отправлен, прерываем выполнение метода
                                    if(snapshot.child(friendID).child("friends").hasChild(
                                            Objects.requireNonNull(Utils.HashMD5(user.getUsername())))) {
                                        Toast.makeText(FriendsActivity.this,
                                                "Запрос уже отправлен", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Friend.send(user.getUsername(), friendName);
                                    Toast.makeText(FriendsActivity.this,
                                            "Запрос отправлен", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(FriendsActivity.this,
                                            "Пользователь не найден", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }).setNegativeButton("Отмена", null)
            .create().show();
        });

    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerView);
        notHasFriends = findViewById(R.id.notHasFriends);
        progressBar = findViewById(R.id.progressBar);
        addFriend = findViewById(R.id.addFriend);
    }

    public FriendsViewModel getModel() {
        return model;
    }

    public User getUser() {
        return user;
    }

    public static class FriendsViewModel extends ViewModel {
        private MutableLiveData<List<Friend>> friends = new MutableLiveData<>();

        void setFriends(List<Friend> friends) {
            this.friends.postValue(friends);
        }

        MutableLiveData<List<Friend>> getFriends() {
            return friends;
        }
    }
    public void openChat(Friend friend){
        Intent intent = new Intent();
        intent.putExtra("friend", friend);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
