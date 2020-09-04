package com.example.minichat.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.MainActivity;
import com.example.minichat.R;

public class HomeFragment extends Fragment {
    private View root;
    private RecyclerView recyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        bindViews();
        //Задание адаптера
        ChatAdapter chatAdapter = new ChatAdapter((MainActivity) requireActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(chatAdapter);
        return root;
    }

    private void bindViews() {
        recyclerView = root.findViewById(R.id.list);
    }
}
