package com.example.minichat.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.data.User;
import com.example.minichat.friends.FriendsActivity;
import com.example.minichat.preMain.SplashActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class DashboardFragment extends Fragment {
    private View root;
    private ImageView userImage;
    private TextView username;
    private Button friends, signOut, changeImage;
    private MainActivity activity;
    private static final int RESULT_LOAD_IMAGE = 1942;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity = (MainActivity) requireActivity();
        bindViews();
        activity.getModel().getUserObservable().observe(this, user -> {
            username.setText(user.getUsername());
            if(user.isHasImage())
                Utils.tryLoadImage(user.getUsername(), userImage);

        });
        friends.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), FriendsActivity.class);
            intent.putExtra("user", activity.getModel().getUserObservable().getValue());
            requireActivity().startActivityForResult(intent, MainActivity.FRIENDS_CODE);
        });
        signOut.setOnClickListener(view -> {
            Intent intent = new Intent(requireActivity(), SplashActivity.class);
            intent.putExtra("signOut", true);
            requireActivity().finish();
            startActivity(intent);
        });
        changeImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
        });

        return root;
    }

    private void bindViews() {
        username = root.findViewById(R.id.username);
        friends = root.findViewById(R.id.friends);
        userImage = root.findViewById(R.id.userImage);
        signOut = root.findViewById(R.id.signout);
        changeImage = root.findViewById(R.id.changeImage);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageUri);
                if(bitmap.getByteCount() > Utils.ONE_MBYTE){
                    Toast.makeText(requireContext(), "Размер изображения слишком велик", Toast.LENGTH_LONG).show();
                    return;
                }
                userImage.setImageBitmap(bitmap);
                User user = activity.getModel().getUserObservable().getValue();
                assert imageUri != null;
                assert user != null;
                FirebaseStorage.getInstance().getReference(User.USER_REFERENCE_PATH)
                        .child(Objects.requireNonNull(Utils.HashMD5(user.getUsername()))).putFile(imageUri)
                        .addOnSuccessListener(runnable -> Toast.makeText(requireContext(), "Аватар изменен", Toast.LENGTH_LONG));
                user.getReference().child("hasImage").setValue(true);

            } catch (IOException e) {
                Toast.makeText(requireContext(), "Неудается прочитать изображение", Toast.LENGTH_LONG).show();
            }


        }
    }
}
