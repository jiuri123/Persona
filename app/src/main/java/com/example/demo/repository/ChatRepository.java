package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatApiMessage;
import com.example.demo.network.ChatRequest;
import com.example.demo.network.ChatResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";

    private static final String MODEL_NAME = "moonshot-v1-8k";

    private final ApiService apiService;

    private final MutableLiveData<List<ChatMessage>> chatHistoryLiveData;
    private final List<ChatApiMessage> apiHistory;

    public ChatRepository(Persona persona) {
        this.apiService = ApiClient.getApiService();
        this.chatHistoryLiveData = new MutableLiveData<>();
        this.apiHistory = new ArrayList<>();

        String systemPrompt = "你现在扮演 " + persona.getName() + "。" +
                "你的背景故事是：" + persona.getBackgroundStory() + "。" +
                "你的简介是：" + persona.getBio() + "。" +
                "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

        this.apiHistory.add(new ChatApiMessage("system", systemPrompt));

        this.chatHistoryLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistoryLiveData;
    }

    public void sendMessage(String userMessageText) {

        ChatMessage uiUserMessage = new ChatMessage(userMessageText, true);
        List<ChatMessage> currentUiHistory = chatHistoryLiveData.getValue();
        if (currentUiHistory == null) {
            currentUiHistory = new ArrayList<>();
        }
        currentUiHistory.add(uiUserMessage);
        chatHistoryLiveData.setValue(currentUiHistory);

        apiHistory.add(new ChatApiMessage("user", userMessageText));
        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory);

        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String aiContent = response.body().getFirstMessageContent();

                    if (aiContent != null) {
                        ChatMessage uiAiMessage = new ChatMessage(aiContent, false);
                        apiHistory.add(new ChatApiMessage("assistant", aiContent));

                        List<ChatMessage> updatedUiHistory = chatHistoryLiveData.getValue();
                        if (updatedUiHistory != null) {
                            updatedUiHistory.add(uiAiMessage);
                            chatHistoryLiveData.postValue(updatedUiHistory);
                        }
                    } else {
                        handleApiError("API 返回了空内容");
                    }
                } else {
                    handleApiError("API 错误: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                handleApiError("网络请求失败: " + t.getMessage());
            }
        });
    }

    private void handleApiError(String errorMessage) {
        ChatMessage errorReply = new ChatMessage("[系统错误: " + errorMessage + "]", false);

        List<ChatMessage> updatedUiHistory = chatHistoryLiveData.getValue();
        if (updatedUiHistory != null) {
            updatedUiHistory.add(errorReply);
            chatHistoryLiveData.postValue(updatedUiHistory);
        }
    }
}