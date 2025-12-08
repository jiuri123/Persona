package com.example.demo.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.R;
import com.example.demo.data.local.AppDatabase;
import com.example.demo.data.local.ChatHistoryDao;
import com.example.demo.data.model.ChatHistory;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.OtherPersona;
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

    // 当前聊天的OtherPersona
    private OtherPersona currentPersona;
    
    // 应用上下文
    private Context context;
    
    // 数据库实例
    private AppDatabase database;
    
    // 聊天历史记录Dao
    private ChatHistoryDao chatHistoryDao;

    /**
     * 私有构造函数，防止外部实例化
     */
    private OtherPersonaChatRepository() {
        this.apiService = ApiClient.getApiService();
        this.chatHistoryLiveData = new MutableLiveData<>();
        this.chatHistoryMap = new HashMap<>();
        this.apiHistoryMap = new HashMap<>();
    }

    /**
     * 获取单例实例
     * @param context 应用上下文
     * @return OtherPersonaChatRepository的单例实例
     */
    public static synchronized OtherPersonaChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new OtherPersonaChatRepository();
            // 初始化上下文和数据库
            instance.context = context.getApplicationContext();
            instance.database = AppDatabase.getInstance(instance.context);
            instance.chatHistoryDao = instance.database.chatHistoryDao();
        }
        return instance;
    }

    /**
     * 设置当前聊天的OtherPersona
     * @param persona 要设置的OtherPersona对象
     */
    public void setCurrentPersona(OtherPersona persona) {
        this.currentPersona = persona;

        // 构建系统提示，设置AI的角色和行为
        String name = persona.getName();
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
        
        // 从数据库加载聊天历史记录
        loadChatHistoryFromDatabase(persona);
    }
    
    /**
     * 从数据库加载聊天历史记录
     * @param persona 当前聊天的OtherPersona对象
     */
    private void loadChatHistoryFromDatabase(OtherPersona persona) {
        new Thread(() -> {
            // 从数据库查询聊天历史记录（使用同步方法）
            List<ChatHistory> chatHistories = database.chatHistoryDao().getChatHistoryByPersonaSync("other", persona.getId());
            List<ChatMessage> personaChatHistory = new ArrayList<>();
            
            if (chatHistories != null && !chatHistories.isEmpty()) {
                // 将ChatHistory转换为ChatMessage
                for (ChatHistory chatHistory : chatHistories) {
                    personaChatHistory.add(ChatMessage.fromChatHistory(chatHistory));
                }
            }
            
            // 更新内存中的聊天历史记录
            chatHistoryMap.put(persona.getName(), personaChatHistory);
            // 更新LiveData，UI将显示该Persona的聊天历史
            chatHistoryLiveData.postValue(personaChatHistory);
        }).start();
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

        // 创建用户消息并添加到UI历史（使用默认用户头像）
        ChatMessage uiUserMessage = new ChatMessage(userMessageText, true, R.drawable.icon_persona, null);
        List<ChatMessage> currentUiHistory = chatHistoryLiveData.getValue();
        if (currentUiHistory == null) {
            currentUiHistory = new ArrayList<>();
        }
        currentUiHistory.add(uiUserMessage);
        chatHistoryLiveData.setValue(currentUiHistory);
        
        // 保存用户消息到数据库
        saveMessageToDatabase(uiUserMessage);
        
        // 获取当前Persona的API历史
        List<ApiRequestMessage> currentApiHistory = apiHistoryMap.get(currentPersona.getName());
        if (currentApiHistory == null) {
            currentApiHistory = new ArrayList<>();
        }
        // 添加用户消息到API历史
        currentApiHistory.add(new ApiRequestMessage("user", userMessageText));
        ApiRequest request = new ApiRequest(BuildConfig.MODEL_NAME, currentApiHistory);

        // 异步调用API
        apiService.getApiResponse(BuildConfig.API_KEY, request).enqueue(new Callback<ApiResponse>() {
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
                        ChatMessage aiMessage = new ChatMessage(aiContent, false, currentPersona.getAvatarDrawableId(), currentPersona.getAvatarUri());
                        if (uiHistory != null) {
                            uiHistory.add(aiMessage);
                        }
                        // 使用postValue在后台线程更新LiveData
                        chatHistoryLiveData.postValue(uiHistory);
                        
                        // 保存AI消息到数据库
                        saveMessageToDatabase(aiMessage);
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
     * 将消息保存到数据库
     * @param message 聊天消息对象
     */
    private void saveMessageToDatabase(ChatMessage message) {
        if (currentPersona == null) {
            return;
        }
        
        new Thread(() -> {
            // 将ChatMessage转换为ChatHistory
            ChatHistory chatHistory = message.toChatHistory("other", currentPersona.getId());
            // 保存到数据库
            database.chatHistoryDao().insert(chatHistory);
        }).start();
    }

    /**
     * 处理API错误
     * @param errorMessage 错误信息
     */
    private void handleApiError(String errorMessage) {
        // 创建错误消息并添加到聊天历史
        ChatMessage errorReply = new ChatMessage("[系统错误: " + errorMessage + "]", false, currentPersona.getAvatarDrawableId(), currentPersona.getAvatarUri());

        List<ChatMessage> updatedUiHistory = chatHistoryLiveData.getValue();
        if (updatedUiHistory != null) {
            updatedUiHistory.add(errorReply);
            chatHistoryLiveData.postValue(updatedUiHistory);
            
            // 保存错误消息到数据库
            saveMessageToDatabase(errorReply);
        }
    }
    
    /**
     * 更新消息的打字机完成状态
     * @param messageId 消息ID
     * @param isComplete 打字机效果是否已完成
     */
    public void updateMessageTypewriterStatus(String messageId, boolean isComplete) {
        if (currentPersona == null) {
            return;
        }
        
        new Thread(() -> {
            // 更新数据库中的打字机完成状态
            database.chatHistoryDao().updateTypewriterStatus(messageId, isComplete);
        }).start();
    }
}