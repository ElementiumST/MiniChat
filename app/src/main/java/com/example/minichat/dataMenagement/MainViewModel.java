package com.example.minichat.dataMenagement;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.minichat.data.Chat;
import com.example.minichat.data.User;

import java.util.List;

// View model для Main activity
public class MainViewModel extends ViewModel {
    private MutableLiveData<User> userObservable = new MutableLiveData<>();
    private MutableLiveData<List<Chat>> chatsObservable = new MutableLiveData<>();

    public MutableLiveData<User> getUserObservable() {
        return userObservable;
    }

    public MutableLiveData<List<Chat>> getChatsObservable() {
        return chatsObservable;
    }

    void setChatsObservable(List<Chat> chats) {
        chatsObservable.setValue(chats);
    }

    public void createConnectionsAndListenersForDatabase(User user) {
        userObservable.setValue(user);
        // запускает поток подгрузки данных
        DataUploadThread thread = new DataUploadThread(user, this);
        thread.start();
    }
}
