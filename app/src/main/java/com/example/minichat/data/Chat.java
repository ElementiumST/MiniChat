package com.example.minichat.data;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class Chat implements Serializable {
    public static final String CHAT_REFERENCE_PATH = "chats";
    private List<String> members;
    private String chatName, key;
    private Message lastMessage;

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Chat() {
    }



    public Chat(String key, String chatName, List<String> members) {
        this.members = members;
        this.chatName = chatName;
        this.key = key;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getChatName() {
        return chatName;
    }

    public String getKey() {
        return key;
    }

    @Exclude
    public DatabaseReference getReference(){
        return FirebaseDatabase.getInstance().getReference(CHAT_REFERENCE_PATH).child(key);
    }
}
