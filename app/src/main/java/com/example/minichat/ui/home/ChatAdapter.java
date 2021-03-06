package com.example.minichat.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.Chat.ChatActivity;
import com.example.minichat.MainActivity;
import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.data.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    MainActivity activity;
    private List<Chat> chatList = new ArrayList<>();

    public ChatAdapter(MainActivity activity) {
        if(activity.getModel().getChatsObservable().getValue() != null){
            chatList = activity.getModel().getChatsObservable().getValue();
            notifyDataSetChanged();
        }
        activity.getModel().getChatsObservable().observe(activity, chats -> {
            chatList = chats;
            notifyDataSetChanged();
        });
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View root = inflater.inflate(R.layout.adapter_chat, parent, false);
        return new ChatViewHolder(root, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView title, lastMessage;
        Chat chat;
        ImageView userImage;
        MainActivity activity;
        ChatViewHolder(@NonNull View itemView, MainActivity activity) {
            super(itemView);
            this.activity = activity;
            title = itemView.findViewById(R.id.username);
            userImage = itemView.findViewById(R.id.userImage);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ChatActivity.class);
                intent.putExtra("chat", chat);
                intent.putExtra("user", activity.getModel().getUserObservable().getValue());
                activity.startActivity(intent);
            });
        }

        @SuppressLint("SetTextI18n")
        void bind(Chat chat) {
            this.chat = chat;
            if(chat.getLastMessage() == null)
                lastMessage.setText("Новых сообщений нет");
            else
                lastMessage.setText(chat.getLastMessage().getOwner() + ": " +chat.getLastMessage().getText());
            title.setText(chat.getChatName());
            String username = Objects.requireNonNull(activity.getModel().getUserObservable().getValue()).getUsername();
            for (String name:
                 chat.getMembers()) {
                if(!name.equals(username)) {
                    Utils.tryLoadImage(name, userImage);
                    break;
                }
            }

        }
    }
}
