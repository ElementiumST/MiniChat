package com.example.minichat.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.minichat.R;
import com.example.minichat.data.Chat;
import com.example.minichat.data.Message;
import com.example.minichat.data.User;
import com.example.minichat.dataMenagement.MainViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    TextView textField;
    RecyclerView list;
    ImageButton sendMessage;
    Chat chat;
    User user;
    ChatViewModel chatViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bindViews();
        chat = (Chat) getIntent().getSerializableExtra("chat");
        user = (User) getIntent().getSerializableExtra("user");
        //Устанавливаем пораметры Recycler view
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        list.setLayoutManager(manager);
        list.setHasFixedSize(true);

        //Отправка сообщения по нажатию
        sendMessage.setOnClickListener(view -> {
            if(textField.getText().toString().length() < 1) return;
            Message message = new Message(user.getUsername(), textField.getText().toString());
            chat.getReference().child("lastMessage").setValue(message);
            chat.getReference().child(Message.MESSAGE_REFERENCE_PATH).push().setValue(message);
            textField.setText("");

        });
        //Создаем адаптер
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.setChat(chat);
        MessageAdapter adapter = new MessageAdapter(this);
        list.setAdapter(adapter);
    }

    public ChatViewModel getChatViewModel() {
        return chatViewModel;
    }

    private void bindViews() {
        textField = findViewById(R.id.textField);
        list = findViewById(R.id.list);
        sendMessage = findViewById(R.id.send);
    }
    //View model, которая подгружает данные сообщений
    public static class ChatViewModel extends ViewModel {
        private MutableLiveData<List<Message>> messagesObservable = new MutableLiveData<>();

        MutableLiveData<List<Message>> getMessagesObservable() {
            return messagesObservable;
        }
        public void setChat(Chat chat) {
            chat.getReference().child(Message.MESSAGE_REFERENCE_PATH)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Message> newList =new ArrayList<>();
                    for (DataSnapshot message:
                         snapshot.getChildren()) {
                        newList.add(message.getValue(Message.class));
                    }
                    // переворачиваем, что-бы новые сообщения были "сверху"
                    Collections.reverse(newList);
                    messagesObservable.setValue(newList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
