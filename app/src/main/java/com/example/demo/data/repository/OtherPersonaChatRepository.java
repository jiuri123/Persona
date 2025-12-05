package com.example.demo.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.data.remote.ApiClient;
import com.example.demo.data.remote.ApiService;
import com.example.demo.data.remote.model.ApiRequestMessage;
import com.example.demo.data.remote.model.ApiRequest;
import com.example.demo.data.remote.model.ApiResponse;

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
    private final Map<String, List<ApiRequestMessage>> apiHistoryMap;

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
      * 设置当前聊天的Persona
      * @param persona 要设置的Persona对象
      */
    public void setCurrentPersona(Persona persona) {
        this.currentPersona = persona;

        // 获取或创建该Persona的聊天历史
        List<ChatMessage> personaChatHistory = chatHistoryMap.computeIfAbsent(persona.getName(), k -> new ArrayList<>());
        // 更新LiveData，UI将显示该Persona的聊天历史
        chatHistoryLiveData.setValue(personaChatHistory);

        // 构建系统提示，设置AI的角色和行为
        String name = persona.getName() != null ? persona.getName() : "未知角色";
        String gender = persona.getGender() != null ? persona.getGender() : "未知性别";
        int age = Math.max(persona.getAge(), 0);
        String personality = persona.getPersonality() != null ? persona.getPersonality() : "未知个性";
        String relationship = persona.getRelationship() != null ? persona.getRelationship() : "未知关系";
        String backgroundStory = persona.getBackgroundStory() != null ? persona.getBackgroundStory() : "";
        String signature = persona.getSignature() != null ? persona.getSignature() : "";

        String systemPrompt = "你现在扮演 " + name + "。" +
                "你的性别是：" + gender + "。" +
                "你的年龄是：" + age + "。" +
                "你的性格是：" + personality + "。" +
                "你与我的关系是：" + relationship + "。" +
                "你的背景故事是：" + backgroundStory + "。" +
                "你的个性签名是：" + signature + "。" +
                "请你严格按照这个角色设定进行对话，不要暴露你是一个 AI 模型。";

        // 获取或创建该Persona的API历史，如果是新创建的则添加系统提示
        apiHistoryMap.computeIfAbsent(persona.getName(), k -> {
            List<ApiRequestMessage> history = new ArrayList<>();
            history.add(new ApiRequestMessage("system", systemPrompt));
            return history;
        });
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
        
        // 获取当前Persona的API历史
        List<ApiRequestMessage> currentApiHistory = apiHistoryMap.get(currentPersona.getName());
        if (currentApiHistory == null) {
            currentApiHistory = new ArrayList<>();
        }
        // 添加用户消息到API历史
        currentApiHistory.add(new ApiRequestMessage("user", userMessageText));
        ApiRequest request = new ApiRequest(BuildConfig.MODEL_NAME, currentApiHistory);

        // 异步调用API
        apiService.getChatCompletion(BuildConfig.API_KEY, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 获取AI回复内容
                    String aiContent = response.body().getFirstMessageContent();

                    if (aiContent != null) {
                        List<ApiRequestMessage> apiHistory = apiHistoryMap.get(currentPersona.getName());
                        List<ChatMessage> uiHistory = chatHistoryLiveData.getValue();
                        // 添加AI消息到API历史
                        if (apiHistory != null) {
                            apiHistory.add(new ApiRequestMessage("assistant", aiContent));
                        }
                        // 添加AI消息到UI历史
                        if (uiHistory != null) {
                            uiHistory.add(new ChatMessage(aiContent, false));
                        }
                        // 使用postValue在后台线程更新LiveData
                        chatHistoryLiveData.postValue(uiHistory);
                    } else {
                        handleApiError("API 返回了空内容");
                    }
                } else {
                    handleApiError("API 错误: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
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