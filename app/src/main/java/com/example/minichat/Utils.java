package com.example.minichat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.minichat.data.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {
    //Генератор хеш ключа md5
    public static String HashMD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.e("Hashing", "MD5 try to hash error");
        }
        return null;
    }
    private static Map<String, Bitmap> cash = new HashMap<>();
    public static final int ONE_MBYTE = 1024*1024;
    private static FirebaseStorage storage = FirebaseStorage.getInstance();
    @SuppressLint("ResourceType")
    public static void tryLoadImage(String username, ImageView view) {
        if(cash.containsKey(username))
            view.setImageBitmap(cash.get(username));
        storage.getReference().child(User.USER_REFERENCE_PATH)
                .child(Objects.requireNonNull(HashMD5(username)))
                .getBytes(ONE_MBYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            cash.put(username, bitmap);
            if(cash.size() > 100) cash.clear();
            view.setImageBitmap(bitmap);
        }).addOnFailureListener(runnable -> {
            view.setImageResource(R.drawable.ic_person_black_24dp);
        });
    }
}
