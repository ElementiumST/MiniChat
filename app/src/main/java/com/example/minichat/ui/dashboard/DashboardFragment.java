package com.example.minichat.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.friends.FriendsActivity;
import com.example.minichat.preMain.SplashActivity;

public class DashboardFragment extends Fragment {
    private View root;
    private ImageView userImage;
    private TextView username;
    private Button friends, signOut;
    private MainActivity activity;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        activity = (MainActivity) requireActivity();
        bindViews();
        activity.getModel().getUserObservable().observe(this, user -> {
            username.setText(user.getUsername());
            if(user.getImage() != null)
                userImage.setImageBitmap(user.getImage());

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
        return root;
    }

    private void bindViews() {
        username = root.findViewById(R.id.username);
        friends = root.findViewById(R.id.friends);
        userImage = root.findViewById(R.id.userImage);
        signOut = root.findViewById(R.id.signout);
    }
}
