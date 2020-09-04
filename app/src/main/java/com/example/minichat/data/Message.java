package com.example.minichat.data;

import android.icu.util.TimeZone;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;

public class Message implements Serializable {
    public static final String MESSAGE_REFERENCE_PATH = "messages";
    private String owner;
    private String text;

    public Message() {
    }

    public Message(String owner, String text) {
        this.owner = owner;
        this.text = text;
    }

    public String getOwner() {
        return owner;
    }

    public String getText() {
        return text;
    }
    public DatabaseReference getReference(Chat chat) {
        return chat.getReference().child(MESSAGE_REFERENCE_PATH).push();
    }
}
