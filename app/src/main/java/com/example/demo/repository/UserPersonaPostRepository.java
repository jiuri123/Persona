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

import com.example.demo.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Post数据仓库类
 * 负责管理与Post相关的API调用和数据管理
 * 实现Repository模式，封装网络请求和数据管理逻辑
 * 使用单例模式确保全局只有一个实例
 */
public class UserPersonaPostRepository {

    /**
     * 内容回调接口
     * 用于处理AI生成和扩展的结果
     */
    public interface ContentCallback {
        /**
         * 成功回调
         * @param content AI生成/扩展的内容
         */
        void onSuccess(String content);

        /**
         * 失败回调
         * @param error 错误信息
         */
        void onError(String error);
    }

    /**
     * 发布回调接口
     * 用于处理发布帖子的结果
     */
    public interface PublishCallback {
        /**
         * 成功回调
         * @param post 发布的帖子对象
         */
        void onSuccess(Post post);

        /**
         * 失败回调
         * @param error 错误信息
         */
        void onError(String error);
    }
    
    // 单例实例
    private static UserPersonaPostRepository instance;
    
    // API密钥和模型名称从BuildConfig获取
    // BuildConfig中的值从gradle.properties注入
    
    // 网络服务和随机数生成器
    private final ApiService apiService;
    private final Random random = new Random();

    // 构建API请求历史
    List<ChatRequestMessage> apiHistory = new ArrayList<>();

    // LiveData对象，用于观察数据变化
    private final MutableLiveData<List<Post>> userPostsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 私有构造函数
     * 初始化API服务实例
     */
    private UserPersonaPostRepository() {
        this.apiService = ApiClient.getApiService();
        // 系统提示，要求AI返回特定格式的JSON
        String systemPrompt = "你是一个社交媒体动态生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"content\": \"[生成的动态正文，必须包含Markdown格式，如**粗体**、*斜体*、~~删除线~~、列表等]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        apiHistory.add(new ChatRequestMessage("system", systemPrompt));
    }
    
    /**
     * 检测输入内容的主要语言
     * @param content 输入的内容
     * @return true表示中文，false表示英文
     */
    private boolean isChineseContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            // 空输入默认使用中文
            return true;
        }
        
        int chineseCharCount = 0;
        int totalCharCount = 0;
        
        for (char c : content.toCharArray()) {
            if (Character.isWhitespace(c)) {
                continue;
            }
            totalCharCount++;
            // 检测中文字符（Unicode范围：\u4e00-\u9fa5）
            if (c >= '\u4e00' && c <= '\u9fa5') {
                chineseCharCount++;
            }
        }
        
        if (totalCharCount == 0) {
            // 纯符号输入默认使用中文
            return true;
        }
        
        // 如果中文字符占比超过50%，则使用中文
        return (double) chineseCharCount / totalCharCount > 0.5;
    }
    
    /**
     * 获取单例实例
     * @return MyPersonaPostRepository的单例实例
     */
    public static synchronized UserPersonaPostRepository getInstance() {
        if (instance == null) {
            instance = new UserPersonaPostRepository();
        }
        return instance;
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
     * 获取我的历史帖子LiveData
     * @return 我的帖子列表的LiveData对象
     */
    public LiveData<List<Post>> getMyPostsLiveData() {
        return userPostsLiveData;
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }

    /**
     * 设置错误信息
     * @param error 错误信息
     */
    public void setError(String error) {
        errorLiveData.setValue(error);
    }

    /**
     * 生成新动态
     * 调用AI API生成基于用户角色的社交媒体动态
     * @param currentUser 当前用户的Persona对象
     */
    public void generateNewPost(Persona currentUser) {
        // 调用aiGenerateContent函数生成内容，然后在回调中处理结果
        aiGenerateContent(currentUser, new ContentCallback() {
            @Override
            public void onSuccess(String generatedContent) {
                // 创建新的Post对象
                Post newPost = new Post(
                        currentUser,
                        generatedContent,
                        null,
                        "刚刚",
                        true
                );

                // 将新帖子添加到历史帖子列表
                List<Post> historyPosts = userPostsLiveData.getValue();
                if (historyPosts == null) {
                    historyPosts = new ArrayList<>();
                }
                historyPosts.add(0, newPost); // 添加到列表顶部
                userPostsLiveData.postValue(historyPosts);
            }

            @Override
            public void onError(String error) {
                // 设置错误信息
                errorLiveData.postValue(error);
            }
        });
    }

    /**
     * AI扩展当前内容
     * 根据当前用户的Persona设定扩展已有内容
     * @param currentUser 当前用户的Persona对象
     * @param currentContent 当前编辑框中的内容
     * @param callback 回调接口，用于处理结果
     */
    public void aiExpandContent(Persona currentUser, String currentContent, ContentCallback callback) {
        // 如果正在加载，则不执行新的请求
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            callback.onError("正在处理请求，请稍后再试");
            return;
        }
        
        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 生成随机数和语言选择
        int randomNumber = random.nextInt(10000);
        boolean isChinese = isChineseContent(currentContent);
        String languageInstruction = isChinese ? 
                "请用中文扩展这条动态。" : 
                "请用英文扩展这条动态。";

        // 用户提示，包含角色信息和扩展要求
        String userPrompt = "请你扮演以下角色：" +
                "名称: " + currentUser.getName() + "\n" +
                "简介: " + currentUser.getBio() + "\n" +
                "背景故事: " + currentUser.getBackgroundStory() + "\n" +
                "请用这个角色的口吻，扩展以下内容：" +
                "原始内容: " + currentContent + "\n" +
                languageInstruction +
                "扩展后的内容必须包含以下Markdown格式中的至少3种：" +
                "1. **粗体文本** (用**文本**表示)" +
                "2. *斜体文本* (用*文本*表示)" +
                "3. ~~删除线~~ (用~~文本~~表示)" +
                "4. 列表 (用- 项目或1. 项目表示)" +
                "5. [链接文本](URL) (用[文本](URL)表示)" +
                "6. `代码` (用`代码`表示)" +
                "请确保扩展后的内容简洁明了，字数控制在50-150字之间。" +
                "(请求编号: " + randomNumber + ")";

        apiHistory.add(new ChatRequestMessage("user", userPrompt));
        
        // 创建聊天请求
        ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(BuildConfig.API_KEY, request).enqueue(new Callback<ChatResponse>() {
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
                            String expandedContent = jsonResponse.getString("content");
                            callback.onSuccess(expandedContent);
                        } catch (JSONException e) {
                            // JSON解析错误
                            callback.onError("AI 返回的数据格式错误");
                        }
                    } else {
                        // API返回空内容
                        callback.onError("API 返回了空内容");
                    }
                } else {
                    // API错误
                    callback.onError("API 错误: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // 请求失败，设置加载状态为false
                isLoadingLiveData.postValue(false);
                // 网络错误
                callback.onError("网络请求失败: " + t.getMessage());
            }
        });
    }

    /**
     * AI生成新内容
     * 根据当前用户的Persona设定生成新内容
     * @param currentUser 当前用户的Persona对象
     * @param callback 回调接口，用于处理结果
     */
    public void aiGenerateContent(Persona currentUser, ContentCallback callback) {
        // 如果正在加载，则不执行新的请求
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            callback.onError("正在处理请求，请稍后再试");
            return;
        }
        
        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 生成随机数和语言选择
        int randomNumber = random.nextInt(10000);
        boolean useEnglish = random.nextBoolean();
        String languageInstruction = useEnglish ? 
                "请用英文写这条动态。" : 
                "请用中文写这条动态。";

        // 用户提示，包含角色信息和生成要求
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
                "请确保动态内容简洁明了，字数控制在50-100字之间。" +
                "(请求编号: " + randomNumber + ")";

        apiHistory.add(new ChatRequestMessage("user", userPrompt));
        
        // 创建聊天请求
        ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(BuildConfig.API_KEY, request).enqueue(new Callback<ChatResponse>() {
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
                            String generatedContent = jsonResponse.getString("content");
                            callback.onSuccess(generatedContent);
                        } catch (JSONException e) {
                            // JSON解析错误
                            callback.onError("AI 返回的数据格式错误");
                        }
                    } else {
                        // API返回空内容
                        callback.onError("API 返回了空内容");
                    }
                } else {
                    // API错误
                    callback.onError("API 错误: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // 请求失败，设置加载状态为false
                isLoadingLiveData.postValue(false);
                // 网络错误
                callback.onError("网络请求失败: " + t.getMessage());
            }
        });
    }

    /**
     * 发布帖子
     * 将编辑好的内容发布为新帖子
     * @param currentUser 当前用户的Persona对象
     * @param content 要发布的内容
     * @param callback 回调接口，用于处理结果
     */
    public void publishPost(Persona currentUser, String content, PublishCallback callback) {
        // 创建新的Post对象
        Post newPost = new Post(
                currentUser,
                content,
                null,
                "刚刚",
                true
        );

        // 将新帖子添加到历史帖子列表
        List<Post> historyPosts = userPostsLiveData.getValue();
        if (historyPosts == null) {
            historyPosts = new ArrayList<>();
        }
        historyPosts.add(0, newPost); // 添加到列表顶部
        userPostsLiveData.postValue(historyPosts);
        callback.onSuccess(newPost);
    }
}