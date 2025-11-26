package com.example.demo.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.model.ChatMessage;
import com.example.demo.repository.ChatRepository;
import com.example.demo.model.Persona;

import java.util.List;

public class MyPersonaViewModel extends ViewModel {

    private ChatRepository chatRepository;

    private LiveData<List<ChatMessage>> chatHistoryLiveData;

    public void init(Persona persona) {
        if (chatRepository == null) {
            chatRepository = new ChatRepository(persona);
            chatHistoryLiveData = chatRepository.getChatHistory();
        }
    }

    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistoryLiveData;
    }

    public void sendMessage(String messageText) {
        if (chatRepository != null) {
            chatRepository.sendMessage(messageText);
        }
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Persona persona;

        public Factory(Persona persona) {
            this.persona = persona;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            MyPersonaViewModel viewModel = new MyPersonaViewModel();
            viewModel.init(persona);
            return (T) viewModel;
        }
    }
}