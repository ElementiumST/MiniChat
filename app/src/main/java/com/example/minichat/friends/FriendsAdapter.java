package com.example.minichat.friends;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minichat.R;
import com.example.minichat.Utils;
import com.example.minichat.Views.FriendButtonContainer;
import com.example.minichat.data.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private List<Friend> friends = new ArrayList<>();
    private FriendsActivity activity;
    public FriendsAdapter(FriendsActivity activity) {
        this.activity = activity;
        activity.getModel().getFriends().observe(activity, observableData -> {
            friends = observableData;
            notifyDataSetChanged();
        });

    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View root = inflater.inflate(R.layout.adapter_friends, parent, false);
        return new FriendViewHolder(root, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        FriendsActivity activity;
        TextView name, status;
        FriendButtonContainer container;
        Friend friend;
        FriendViewHolder(@NonNull View itemView, FriendsActivity activity) {
            super(itemView);
            this.activity = activity;
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            container = itemView.findViewById(R.id.container);
            container.setClickListeners(new FriendButtonContainer.OnContainerItemClickListener() {
                @Override
                public void OnAccept(View view) {
                    Friend.accept(activity.user.getUsername(), friend.getUsername());
                }

                @Override
                public void OnDelete(View view) {
                    Friend.remove(activity.user.getUsername(), friend.getUsername());

                }

                @Override
                public void OnReject(View view) {
                    Friend.reject(activity.user.getUsername(), friend.getUsername());
                }

                @Override
                public void onChat(View view) {
                    activity.openChat(friend);
                }
            });
        }
        void bind(Friend friend) {
            this.friend = friend;
            name.setText(friend.getUsername());
            container.removeAllViews();
            container.addButtons(friend.getStatus());
            status.setText(friend.getStatusString());

        }
    }

}
