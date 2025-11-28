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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户自己创建的Persona聊天数据仓库类
 * 负责管理与用户自己创建的Persona的聊天API通信和聊天历史记录
 * 实现Repository模式，封装网络请求和数据管理逻辑
 * 使用单例模式确保全局只有一个实例
 */
public class UserPersonaChatRepository {

    // 单例实例
    private static UserPersonaChatRepository instance;

    // API密钥，用于身份验证
    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";

    // 使用的AI模型名称
    private static final String MODEL_NAME = "moonshot-v1-8k";

    // Retrofit API服务接口
    private final ApiService apiService;

    // 聊天历史记录的LiveData，用于UI观察
    private final MutableLiveData<List<ChatMessage>> chatHistoryLiveData;
    
    // API请求历史记录，用于维护上下文
    private final List<ChatRequestMessage> apiHistory;

    // 当前聊天的Persona
    private Persona currentPersona;

    /**
     * 私有构造函数，防止外部实例化
     */
    private UserPersonaChatRepository() {
        this.apiService = ApiClient.getApiService();
        this.chatHistoryLiveData = new MutableLiveData<>();
        this.apiHistory = new ArrayList<>();
        this.chatHistoryLiveData.setValue(new ArrayList<>());
    }

    /**
     * 获取单例实例
     * @return UserPersonaChatRepository的单例实例
     */
    public static synchronized UserPersonaChatRepository getInstance() {
        if (instance == null) {
            instance = new UserPersonaChatRepository();
        }
        return instance;
    }

    /**
     * 设置当前聊天的Persona
     * @param persona 当前聊天的Persona对象
     */
    public void setCurrentPersona(Persona persona) {
        this.currentPersona = persona;
        // 重置聊天历史
        resetChatHistory();
        
        // 构建系统提示，设置AI的角色和行为
        String systemPrompt = "你现在扮演 " + persona.getName() + "。" +
                "你的背景故事是：" + persona.getBackgroundStory() + "。" +
                "你的简介是：" + persona.getBio() + "。" +
                "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

        // 添加系统消息到API历史
        this.apiHistory.add(new ChatRequestMessage("system", systemPrompt));
    }

    /**
     * 重置聊天历史
     */
    public void resetChatHistory() {
        apiHistory.clear();
        chatHistoryLiveData.setValue(new ArrayList<>());
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
        if (currentPersona == null) {
            handleApiError("当前没有设置聊天的Persona");
            return;
        }

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
        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() {
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