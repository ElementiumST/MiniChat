package com.example.minichat.data;

import android.graphics.Bitmap;

import com.example.minichat.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    public static final String USER_REFERENCE_PATH = "users";
    private String username, password, imagePath;
    private Bitmap image;
    private String[] chatIDs;
    private Friend[] friends;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //Необходимо для Firebase
    public User() {}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getImagePath() {
        return imagePath;
    }
    @Exclude
    public Bitmap getImage() {
        return image;
    }

    public String[] getChatIDs() {
        return chatIDs;
    }

    @Exclude
    public DatabaseReference getReference() {
        if(username == null) return null;
        return FirebaseDatabase.getInstance()
                .getReference(USER_REFERENCE_PATH).child(
                        Objects.requireNonNull(Utils.HashMD5(username)));
    }
}
