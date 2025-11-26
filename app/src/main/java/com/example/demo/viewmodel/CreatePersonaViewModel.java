package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatApiMessage;
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

public class CreatePersonaViewModel extends ViewModel {

    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";
    private static final String MODEL_NAME = "moonshot-v1-8k";

    private final ApiService apiService;
    private final Random random = new Random();

    private static final String[] THEMES = {
            "赛博朋克", "奇幻", "科幻", "蒸汽朋克", "神秘", "历史", "艺术家", "探险家", "AI", "时间旅行者"
    };

    private final MutableLiveData<String> generatedNameLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> generatedStoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public CreatePersonaViewModel() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<String> getGeneratedName() {
        return generatedNameLiveData;
    }

    public LiveData<String> getGeneratedStory() {
        return generatedStoryLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void generatePersonaDetails() {
        isLoadingLiveData.setValue(true);

        String systemPrompt = "你是一个富有创造力的人设生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"name\": \"[生成的人设名称]\", \"story\": \"[生成的背景故事，2-3句话]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        int randomNumber = random.nextInt(10000);
        String randomTheme = THEMES[random.nextInt(THEMES.length)];

        String userPrompt = "请为我生成一个独特且有趣的 Persona 角色。" +
                "请让人设带有一点 [" + randomTheme + "] 风格。" +
                " (这是一个新的请求, 编号: " + randomNumber + ")";

        List<ChatApiMessage> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatApiMessage("system", systemPrompt));
        apiHistory.add(new ChatApiMessage("user", userPrompt));

        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory);

        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                isLoadingLiveData.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    String aiContent = response.body().getFirstMessageContent();

                    if (aiContent != null) {
                        try {
                            JSONObject jsonResponse = new JSONObject(aiContent);

                            String name = jsonResponse.getString("name");
                            String story = jsonResponse.getString("story");

                            generatedNameLiveData.postValue(name);
                            generatedStoryLiveData.postValue(story);

                        } catch (JSONException e) {
                            errorLiveData.postValue("AI 返回的数据格式错误: " + e.getMessage());
                        }
                    } else {
                        errorLiveData.postValue("API 返回了空内容");
                    }
                } else {
                    errorLiveData.postValue("API 错误: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }
}