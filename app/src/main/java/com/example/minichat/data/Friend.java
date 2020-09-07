package com.example.minichat.data;

import android.graphics.Bitmap;
import android.widget.Switch;

import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.security.acl.Owner;
import java.util.Objects;

public class Friend implements Serializable {
    public static final String FRIEND_REFERENCE_PATH = "friends";
    private String username;
    private Status status;
    public enum Status {
        SENT,
        RECEIVED,
        CONFIRMED,
        DECLINED
    }

    public Friend(String username, Status status) {
        this.username = username;
        this.status = status;
    }

    public Friend() {
    }

    public String getUsername() {
        return username;
    }

    public Status getStatus() {
        return status;
    }
    //Возвращает строковое сообщение соответствующее статусу запроса
    @Exclude
    public String getStatusString() {
        switch (status) {
            case SENT:
                return  "Запрос отправлен";
            case DECLINED:
                return  "Запрос отклонен";
            case RECEIVED:
                return  "Запрос в друзья";
            case CONFIRMED:
                return  "Ваш друг";
            default:
                return "Нет данных";
        }
    }
    // Отправляет запрос в друзья
    public static void send(String ownerName, String recipientName) {
        String ownerID = Objects.requireNonNull(Utils.HashMD5(ownerName));
        String recipientID = Objects.requireNonNull(Utils.HashMD5(recipientName));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USER_REFERENCE_PATH);
        reference.child(recipientID).child(FRIEND_REFERENCE_PATH).child(ownerID).setValue(new Friend(ownerName, Status.RECEIVED));
        reference.child(ownerID).child(FRIEND_REFERENCE_PATH).child(recipientID).setValue(new Friend(recipientName, Status.SENT));
    }
    // Удаляет из друзей
    public static void remove(String ownerName, String recipientName) {
        String ownerID = Objects.requireNonNull(Utils.HashMD5(ownerName));
        String recipientID = Objects.requireNonNull(Utils.HashMD5(recipientName));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USER_REFERENCE_PATH);
        reference.child(recipientID).child(FRIEND_REFERENCE_PATH).child(ownerID).removeValue();
        reference.child(ownerID).child(FRIEND_REFERENCE_PATH).child(recipientID).removeValue();
    }
    // Принимает заявку в жрузья
    public static void accept(String ownerName, String recipientName) {
        String ownerID = Objects.requireNonNull(Utils.HashMD5(ownerName));
        String recipientID = Objects.requireNonNull(Utils.HashMD5(recipientName));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USER_REFERENCE_PATH);
        reference.child(recipientID).child(FRIEND_REFERENCE_PATH).child(ownerID).setValue(new Friend(ownerName, Status.CONFIRMED));
        reference.child(ownerID).child(FRIEND_REFERENCE_PATH).child(recipientID).setValue(new Friend(recipientName, Status.CONFIRMED));
    }
    // Отклоняет запрос в друзья
    public static void reject(String ownerName, String recipientName) {
        String ownerID = Objects.requireNonNull(Utils.HashMD5(ownerName));
        String recipientID = Objects.requireNonNull(Utils.HashMD5(recipientName));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(User.USER_REFERENCE_PATH);
        reference.child(recipientID).child(FRIEND_REFERENCE_PATH).child(ownerID).setValue(new Friend(ownerName, Status.DECLINED));
        reference.child(ownerID).child(FRIEND_REFERENCE_PATH).child(recipientID).removeValue();
    }

    @Exclude
    public DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference(User.USER_REFERENCE_PATH)
                .child(Objects.requireNonNull(Utils.HashMD5(username)));
    }
}
