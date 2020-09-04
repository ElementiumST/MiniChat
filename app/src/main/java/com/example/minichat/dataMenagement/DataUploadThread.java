package com.example.minichat.dataMenagement;

import androidx.annotation.NonNull;

import com.example.minichat.data.Chat;
import com.example.minichat.data.Message;
import com.example.minichat.data.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Поток, который на фоне подгружает чаты и возврашает их в модель
public class DataUploadThread extends Thread {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private User currentUser;
    private MainViewModel viewModel;
    private HashMap<String, Chat> chatMap = new HashMap<>();

    DataUploadThread(User currentUser, MainViewModel viewModel) {
        this.currentUser = currentUser;
        this.viewModel = viewModel;
    }

    private void addChat(String key, Chat chat) {
        chatMap.put(key, chat);
        List<Chat> collection = new ArrayList<>(chatMap.values());
        viewModel.setChatsObservable(collection);
    }
    private void setUser(User user) {
        this.currentUser = user;
        viewModel.getUserObservable().postValue(user);
    }

    @Override
    public void run() {
        currentUser.getReference().child(Chat.CHAT_REFERENCE_PATH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference reference = database.getReference(Chat.CHAT_REFERENCE_PATH);
                for (DataSnapshot chatIDSnapshot:
                     snapshot.getChildren()) {
                    String chatID = chatIDSnapshot.getValue(String.class);
                    assert chatID != null;
                    reference.child(chatID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Chat chat = snapshot.getValue(Chat.class);
                                    addChat(chatID, chat);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        currentUser.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setUser(snapshot.getValue(User.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
