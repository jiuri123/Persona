package com.example.demo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatMessageDto;
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
 * CreatePersonaActivity 的 ViewModel
 * 负责处理 AI 生成人设的逻辑
 */
public class CreatePersonaViewModel extends ViewModel {

    // --- 1. API 常量和 Service 实例 ---
    // [!!] 我们暂时从 ChatRepository 复制这些常量
    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L"; //
    private static final String MODEL_NAME = "moonshot-v1-8k"; //

    private final ApiService apiService;

    // [!!] 新增这一行：
    private final Random random = new Random();

    // [!! 新增 !!] 一个主题列表，用来“激发”AI 的创意
    private static final String[] THEMES = {
            "赛博朋克", "奇幻", "科幻", "蒸汽朋克", "神秘", "历史", "艺术家", "探险家", "AI", "时间旅行者"
    };

    // --- 2. LiveData (数据持有者) ---
    // Activity 将观察这些 LiveData 来获取 AI 生成的结果
    private final MutableLiveData<String> generatedNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> generatedStoryLiveData = new MutableLiveData<>();

    // (可选，但推荐) 用于通知 UI "正在加载" 和 "出现错误"
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     */
    public CreatePersonaViewModel() {
        // 从 ApiClient 获取 ApiService 的单例
        this.apiService = ApiClient.getApiService();
    }

    // --- 3. LiveData 的 Getters (暴露给 Activity) ---

    public LiveData<String> getGeneratedName() {
        return generatedNameLiveData;
    }

    public LiveData<String> getGeneratedStory() {
        return generatedStoryLiveData;
    }

    /**
     * 获取加载状态的 LiveData
     * 
     * @return 包含布尔值的 LiveData，表示当前是否正在加载数据
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    // --- 4. 触发 AI 生成的方法 (我们将在下一步实现它) ---

    /**
     * Activity 将调用此方法来启动 AI 生成
     */
    public void generatePersonaDetails() {
        // 1. 通知 UI 开始加载
        isLoadingLiveData.setValue(true);

        // --- 2. [!! 核心 !!] 定义 AI 指令 (Prompt) ---

        // a. 系统指令：我们在这里 "教会" AI 如何返回数据
        //    我们强制它必须返回一个特定格式的 JSON
        String systemPrompt = "你是一个富有创造力的人设生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"name\": \"[生成的人设名称]\", \"story\": \"[生成的背景故事，2-3句话]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        // b. [!! 再次修改 !!] 用户指令
        //    我们添加 随机数 和 随机主题，来最大化创意
        int randomNumber = random.nextInt(10000);
        String randomTheme = THEMES[random.nextInt(THEMES.length)]; // 随机选一个主题

        String userPrompt = "请为我生成一个独特且有趣的 Persona 角色。" +
                "请让人设带有一点 [" + randomTheme + "] 风格。" + // [!!] 注入随机主题
                " (这是一个新的请求, 编号: " + randomNumber + ")";

        // --- 3. 准备 API 请求 ---
        List<ChatMessageDto> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatMessageDto("system", systemPrompt)); //
        apiHistory.add(new ChatMessageDto("user", userPrompt)); //

        // 创建请求体
        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory); //

        // 4. 异步调用 API
        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() { //
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                // 5. [!! 核心 !!] 处理 API 响应
                isLoadingLiveData.postValue(false); // 停止加载 (使用 postValue)

                if (response.isSuccessful() && response.body() != null) { //
                    String aiContent = response.body().getFirstMessageContent(); //

                    if (aiContent != null) {
                        try {
                            // a. AI 返回的是一个"包含 JSON 的字符串"
                            //    我们用 org.json.JSONObject 手动解析它
                            JSONObject jsonResponse = new JSONObject(aiContent);

                            // b. 从 JSON 中提取 "name" 和 "story"
                            String name = jsonResponse.getString("name");
                            String story = jsonResponse.getString("story");

                            // c. 将数据发送回 Activity
                            generatedNameLiveData.postValue(name);
                            generatedStoryLiveData.postValue(story);

                        } catch (JSONException e) {
                            // d. 如果 AI 返回的不是标准 JSON，则报错
                            errorLiveData.postValue("AI 返回的数据格式错误: " + e.getMessage());
                        }
                    } else {
                        errorLiveData.postValue("API 返回了空内容");
                    }
                } else {
                    // API Key 错误, 服务器错误等
                    errorLiveData.postValue("API 错误: " + response.code() + " " + response.message()); //
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                // 6. 处理网络错误
                isLoadingLiveData.postValue(false); // 停止加载
                errorLiveData.postValue("网络请求失败: " + t.getMessage()); //
            }
        });
    }

}