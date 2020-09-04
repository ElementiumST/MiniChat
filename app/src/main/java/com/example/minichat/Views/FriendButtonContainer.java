package com.example.minichat.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;

import com.example.minichat.R;
import com.example.minichat.data.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendButtonContainer extends LinearLayout {
    public FriendButtonContainer(Context context) {
        super(context);
    }

    public FriendButtonContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private OnContainerItemClickListener listener;
    public void setClickListeners(OnContainerItemClickListener listeners){
        this.listener = listeners;
    }
    public void addButtons(Friend.Status status) {
        if(listener == null) {
            Log.e("ContainerException", "Сначала обьявите слушатель");
            throw new IllegalArgumentException();
        }
        Context context = getContext();
        LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        if(status == Friend.Status.RECEIVED){
            Button accept = new Button(new ContextThemeWrapper(context, R.style.Accept), null, 0);
            accept.setOnClickListener(view -> listener.OnAccept(view));
            addView(accept, params);
            Button reject = new Button(new ContextThemeWrapper(context, R.style.Reject), null, 0);
            reject.setOnClickListener(view -> listener.OnReject(view));
            addView(reject, params);
            // кнопка "Удалить" нам не нужна, так что завершаем выполнение метода
            return;
        } else if(status == Friend.Status.CONFIRMED) {
            Button chat = new Button(new ContextThemeWrapper(context, R.style.Chat), null, 0);
            chat.setOnClickListener(view -> listener.onChat(view));
            addView(chat, params);
        }
        Button delete = new Button(new ContextThemeWrapper(context, R.style.Delete), null, 0);
        delete.setOnClickListener(view -> listener.OnDelete(view));
        addView(delete, params);

    }
    public interface OnContainerItemClickListener{
        void OnAccept(View view);
        void OnDelete(View view);
        void OnReject(View view);
        void onChat(View view);
    }
}
