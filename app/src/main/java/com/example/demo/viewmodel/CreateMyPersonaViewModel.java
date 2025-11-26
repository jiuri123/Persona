package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatRequestMessage;
import com.example.demo.network.ChatRequest;
import com.example.demo.network.ChatResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建角色ViewModel类
 * 管理角色创建相关的数据和操作
 * 负责与AI API交互生成角色名称和背景故事
 * 使用LiveData观察数据变化，通知UI更新
 */
public class CreateMyPersonaViewModel extends ViewModel {

    // API密钥和模型名称常量
    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";
    private static final String MODEL_NAME = "moonshot-v1-8k";

    // 网络服务和随机数生成器
    private final ApiService apiService;
    private final Random random = new Random();

    // 角色主题数组
    private static final String[] THEMES = {
            "赛博朋克", "奇幻", "科幻", "蒸汽朋克", "神秘", "历史", "艺术家", "探险家", "AI", "时间旅行者"
    };

    // LiveData对象，用于观察数据变化
    private final MutableLiveData<String> generatedNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> generatedStoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化API服务实例
     */
    public CreateMyPersonaViewModel() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * 获取生成的角色名称LiveData
     * @return 角色名称的LiveData对象
     */
    public LiveData<String> getGeneratedName() {
        return generatedNameLiveData;
    }

    /**
     * 获取生成的角色故事LiveData
     * @return 角色故事的LiveData对象
     */
    public LiveData<String> getGeneratedStory() {
        return generatedStoryLiveData;
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
     * 生成角色详情
     * 调用AI API生成角色名称和背景故事
     * 使用随机主题增加角色多样性
     */
    public void generatePersonaDetails() {
        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 系统提示，要求AI返回特定格式的JSON
        String systemPrompt = "你是一个富有创造力的人设生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"name\": \"[生成的人设名称]\", \"story\": \"[生成的背景故事，2-3句话]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        // 生成随机数和随机主题
        int randomNumber = random.nextInt(10000);
        String randomTheme = THEMES[random.nextInt(THEMES.length)];

        // 用户提示，包含随机主题
        String userPrompt = "请为我生成一个独特且有趣的 Persona 角色。" +
                "请让人设带有一点 [" + randomTheme + "] 风格。" +
                " (这是一个新的请求, 编号: " + randomNumber + ")";

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

                            String name = jsonResponse.getString("name");
                            String story = jsonResponse.getString("story");

                            // 更新LiveData，通知UI更新
                            generatedNameLiveData.postValue(name);
                            generatedStoryLiveData.postValue(story);

                        } catch (JSONException e) {
                            // JSON解析错误
                            errorLiveData.postValue("AI 返回的数据格式错误: " + e.getMessage());
                        }
                    } else {
                        // API返回空内容
                        errorLiveData.postValue("API 返回了空内容");
                    }
                } else {
                    // API错误
                    errorLiveData.postValue("API 错误: " + response.code() + " " + response.message());
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