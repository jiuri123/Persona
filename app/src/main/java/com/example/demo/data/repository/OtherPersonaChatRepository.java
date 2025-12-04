package com.example.demo.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.data.remote.ApiClient;
import com.example.demo.data.remote.ApiService;
import com.example.demo.data.remote.model.ChatRequestMessage;
import com.example.demo.data.remote.model.ChatRequest;
import com.example.demo.data.remote.model.ChatResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 单例实例
    private static OtherPersonaChatRepository instance;

    // Retrofit API服务接口
    private final ApiService apiService;

    // 聊天历史记录的LiveData，用于UI观察当前Persona的聊天记录
    private final MutableLiveData<List<ChatMessage>> chatHistoryLiveData;
    
    // 存储所有Persona的聊天历史记录，以Persona名称为key
    private final Map<String, List<ChatMessage>> chatHistoryMap;
    
    // 存储所有Persona的API请求历史记录，以Persona名称为key
    private final Map<String, List<ChatRequestMessage>> apiHistoryMap;

    // 当前聊天的Persona
    private Persona currentPersona;

    /**
     * 构造函数
     */
    public OtherPersonaChatRepository() {
        this.apiService = ApiClient.getApiService();
        this.chatHistoryLiveData = new MutableLiveData<>();
        this.chatHistoryMap = new HashMap<>();
        this.apiHistoryMap = new HashMap<>();
    }

    /**
     * 获取单例实例
     * @return UserPersonaChatRepository的单例实例
     */
    public static synchronized OtherPersonaChatRepository getInstance() {
        if (instance == null) {
            instance = new OtherPersonaChatRepository();
        }
        return instance;
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

        // 构建系统提示，设置AI的角色和行为
        String systemPrompt = "你现在扮演 " + currentPersona.getName() + "。" +
                "你的背景故事是：" + currentPersona.getBackgroundStory() + "。" +
                "你的个性签名是：" + currentPersona.getSignature() + "。" +
                "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

        // 获取当前Persona的API历史，如果不存在则创建并添加系统提示
        List<ChatRequestMessage> currentApiHistory = apiHistoryMap.computeIfAbsent(currentPersona.getName(), k -> {
            List<ChatRequestMessage> history = new ArrayList<>();
            history.add(new ChatRequestMessage("system", systemPrompt));
            return history;
        });

        // 创建用户消息并添加到UI历史
        ChatMessage uiUserMessage = new ChatMessage(userMessageText, true);
        List<ChatMessage> currentUiHistory = chatHistoryLiveData.getValue();
        if (currentUiHistory == null) {
            currentUiHistory = new ArrayList<>();
        }
        currentUiHistory.add(uiUserMessage);

        chatHistoryLiveData.setValue(currentUiHistory);
        // 添加用户消息到API历史
        currentApiHistory.add(new ChatRequestMessage("user", userMessageText));

        ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, currentApiHistory);

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
                        // 添加AI回复到当前Persona的API历史
                        currentApiHistory.add(new ChatRequestMessage("assistant", aiContent));

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

     /**
      * 设置当前聊天的Persona
      * @param persona 要设置的Persona对象
      */
    public void setCurrentPersona(Persona persona) {
        this.currentPersona = persona;
        // 获取或创建该Persona的聊天历史
        List<ChatMessage> personaChatHistory = chatHistoryMap.computeIfAbsent(persona.getName(), k -> new ArrayList<>());
        // 更新LiveData，UI将显示该Persona的聊天历史
        chatHistoryLiveData.setValue(personaChatHistory);
    }
}