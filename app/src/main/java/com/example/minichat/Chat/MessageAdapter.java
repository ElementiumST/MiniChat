package com.example.minichat.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.data.Chat;
import com.example.minichat.data.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ChatActivity activity;
    private List<Message> messages = new ArrayList<>();

    public MessageAdapter(ChatActivity activity) {
        this.activity = activity;
        activity.getChatViewModel().getMessagesObservable().observe(activity,messages1 -> {
            messages = messages1;
            notifyDataSetChanged();
        });
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View root = inflater.inflate(R.layout.adapter_message, parent, false);
        return new MessageViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView owner, text;
        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            owner = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.text);

        }
        void bind(Message message) {
            owner.setText(message.getOwner());
            text.setText(message.getText());
        }
    }
}
