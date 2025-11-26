package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatRequestMessage;
import com.example.demo.network.ChatRequest;
import com.example.demo.network.ChatResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Post数据仓库类
 * 负责管理与Post相关的API调用和数据管理
 * 实现Repository模式，封装网络请求和数据管理逻辑
 */
public class MyPersonaPostRepository {

    // API密钥和模型名称常量
    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";
    private static final String MODEL_NAME = "moonshot-v1-8k";
    
    // 网络服务和随机数生成器
    private final ApiService apiService;
    private final Random random = new Random();

    // LiveData对象，用于观察数据变化
    private final MutableLiveData<Post> newPostLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化API服务实例
     */
    public MyPersonaPostRepository() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * 获取新动态LiveData
     * @return 新动态的LiveData对象
     */
    public LiveData<Post> getNewPostLiveData() {
        return newPostLiveData;
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }

    /**
     * 生成新动态
     * 调用AI API生成基于用户角色的社交媒体动态
     * @param currentUser 当前用户的Persona对象
     */
    public void generateNewPost(Persona currentUser) {
        if (currentUser == null) {
            errorLiveData.setValue("请先在 '我的 Persona' 标签页中创建一个人设");
            return;
        }

        // 如果正在加载，则不执行新的请求
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            return;
        }
        
        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 系统提示，要求AI返回特定格式的JSON
        String systemPrompt = "你是一个社交媒体动态生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"content\": \"[生成的动态正文，必须包含Markdown格式，如**粗体**、*斜体*、~~删除线~~、列表等]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        // 生成随机数和语言选择
        int randomNumber = random.nextInt(10000);
        boolean useEnglish = random.nextBoolean();
        String languageInstruction = useEnglish ? 
                "请用英文写这条动态。" : 
                "请用中文写这条动态。";
        
        // 用户提示，包含角色信息和动态要求
        String userPrompt = "请你扮演以下角色：" +
                "名称: " + currentUser.getName() + "\n" +
                "简介: " + currentUser.getBio() + "\n" +
                "背景故事: " + currentUser.getBackgroundStory() + "\n" +
                "请用这个角色的口吻，写一条全新的、有趣的社交媒体动态。" +
                languageInstruction +
                "这条动态必须包含以下Markdown格式中的至少3种：" +
                "1. **粗体文本** (用**文本**表示)" +
                "2. *斜体文本* (用*文本*表示)" +
                "3. ~~删除线~~ (用~~文本~~表示)" +
                "4. 列表 (用- 项目或1. 项目表示)" +
                "5. [链接文本](URL) (用[文本](URL)表示)" +
                "6. `代码` (用`代码`表示)" +
                "(请求编号: " + randomNumber + ")";

        // 构建API请求历史
        List<ChatRequestMessage> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatRequestMessage("system", systemPrompt));
        apiHistory.add(new ChatRequestMessage("user", userPrompt));
        
        // 创建聊天请求
        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                // 请求完成，设置加载状态为false
                isLoadingLiveData.postValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    // 获取AI返回的内容
                    String aiContent = response.body().getFirstMessageContent();
                    if (aiContent != null) {
                        try {
                            // 解析JSON响应
                            JSONObject jsonResponse = new JSONObject(aiContent);
                            String postContent = jsonResponse.getString("content");

                            // 创建新的Post对象
                            Post newPost = new Post(
                                    currentUser,
                                    postContent,
                                    null,
                                    "刚刚"
                            );

                            // 更新LiveData，通知UI更新
                            newPostLiveData.postValue(newPost);

                        } catch (JSONException e) {
                            // JSON解析错误
                            errorLiveData.postValue("AI 返回的数据格式错误");
                        }
                    } else {
                        // API返回空内容
                        errorLiveData.postValue("API 返回了空内容");
                    }
                } else {
                    // API错误
                    errorLiveData.postValue("API 错误: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // 请求失败，设置加载状态为false
                isLoadingLiveData.postValue(false);
                // 网络错误
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }
}