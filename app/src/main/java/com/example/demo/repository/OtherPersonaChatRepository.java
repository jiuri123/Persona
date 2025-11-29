package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatRequestMessage;
import com.example.demo.network.ChatRequest;
import com.example.demo.network.ChatResponse;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 其他Persona聊天数据仓库类
 * 负责管理与其他Persona的聊天API通信和聊天历史记录
 * 实现Repository模式，封装网络请求和数据管理逻辑
 * 每个实例对应一个特定的Persona
 */
public class OtherPersonaChatRepository {

    // API密钥，从BuildConfig获取
    // BuildConfig中的值从gradle.properties注入
    
    // 使用的AI模型名称，从BuildConfig获取

    // Retrofit API服务接口
    private final ApiService apiService;

    // 聊天历史记录的LiveData，用于UI观察
    private final MutableLiveData<List<ChatMessage>> chatHistoryLiveData;
    
    // API请求历史记录，用于维护上下文
    private final List<ChatRequestMessage> apiHistory;

    // 当前聊天的Persona
    private final Persona currentPersona;

    /**
     * 构造函数
     * @param persona 当前聊天的人格角色
     */
    public OtherPersonaChatRepository(Persona persona) {
        this.currentPersona = persona;
        this.apiService = ApiClient.getApiService();
        this.chatHistoryLiveData = new MutableLiveData<>();
        this.apiHistory = new ArrayList<>();

        // 构建系统提示，设置AI的角色和行为
        String systemPrompt = "你现在扮演 " + persona.getName() + "。" +
                "你的背景故事是：" + persona.getBackgroundStory() + "。" +
                "你的个性签名是：" + persona.getSignature() + "。" +
                "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

        // 添加系统消息到API历史
        this.apiHistory.add(new ChatRequestMessage("system", systemPrompt));

        // 初始化空的聊天历史
        this.chatHistoryLiveData.setValue(new ArrayList<>());
    }

    /**
     * 获取聊天历史记录的LiveData
     * @return 可观察的聊天历史LiveData
     */
    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistoryLiveData;
    }
    
    /**
     * 发送用户消息到API并获取回复
     * @param userMessageText 用户输入的消息文本
     */
    public void sendMessage(String userMessageText) {
        // 创建用户消息并添加到UI历史
        ChatMessage uiUserMessage = new ChatMessage(userMessageText, true);
        List<ChatMessage> currentUiHistory = chatHistoryLiveData.getValue();
        if (currentUiHistory == null) {
            currentUiHistory = new ArrayList<>();
        }
        currentUiHistory.add(uiUserMessage);
        chatHistoryLiveData.setValue(currentUiHistory);

        // 添加用户消息到API历史
        apiHistory.add(new ChatRequestMessage("user", userMessageText));
        ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(BuildConfig.API_KEY, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 获取AI回复内容
                    String aiContent = response.body().getFirstMessageContent();

                    if (aiContent != null) {
                        // 创建AI消息并添加到UI和API历史
                        ChatMessage uiAiMessage = new ChatMessage(aiContent, false);
                        apiHistory.add(new ChatRequestMessage("assistant", aiContent));

                        List<ChatMessage> updatedUiHistory = chatHistoryLiveData.getValue();
                        if (updatedUiHistory != null) {
                            updatedUiHistory.add(uiAiMessage);
                            // 使用postValue在后台线程更新LiveData
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

    /**
     * 处理API错误
     * @param errorMessage 错误信息
     */
    private void handleApiError(String errorMessage) {
        // 创建错误消息并添加到聊天历史
        ChatMessage errorReply = new ChatMessage("[系统错误: " + errorMessage + "]", false);

        List<ChatMessage> updatedUiHistory = chatHistoryLiveData.getValue();
        if (updatedUiHistory != null) {
            updatedUiHistory.add(errorReply);
            chatHistoryLiveData.postValue(updatedUiHistory);
        }
    }
}