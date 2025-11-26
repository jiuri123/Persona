package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatApiMessage;
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

public class MainViewModel extends ViewModel {

    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L";
    private static final String MODEL_NAME = "moonshot-v1-8k";
    private final ApiService apiService;
    private final Random random = new Random();

    private final MutableLiveData<Persona> userPersonaLiveData = new MutableLiveData<>(null);
    private final MutableLiveData<Post> newPostLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Persona>> followedPersonasLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public MainViewModel() {
        this.apiService = ApiClient.getApiService();
    }

    public LiveData<Post> getNewPostLiveData() {
        return newPostLiveData;
    }

    public LiveData<List<Persona>> getFollowedPersonas() {
        return followedPersonasLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void setUserPersona(Persona persona) {
        userPersonaLiveData.setValue(persona);
    }

    public void generateNewPost() {
        Persona currentUser = userPersonaLiveData.getValue();
        if (currentUser == null) {
            errorLiveData.setValue("请先在 '我的 Persona' 标签页中创建一个人设");
            return;
        }

        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            return;
        }
        isLoadingLiveData.setValue(true);

        String systemPrompt = "你是一个社交媒体动态生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"content\": \"[生成的动态正文，必须包含Markdown格式，如**粗体**、*斜体*、~~删除线~~、列表等]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        int randomNumber = random.nextInt(10000);
        boolean useEnglish = random.nextBoolean();
        String languageInstruction = useEnglish ? 
                "请用英文写这条动态。" : 
                "请用中文写这条动态。";
        
        String userPrompt = "请你扮演以下角色：" +
                "名称: " + currentUser.getName() + "" +
                "简介: " + currentUser.getBio() + "" +
                "背景故事: " + currentUser.getBackgroundStory() + "" +
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
                            String postContent = jsonResponse.getString("content");

                            Post newPost = new Post(
                                    currentUser,
                                    postContent,
                                    null,
                                    "刚刚"
                            );

                            newPostLiveData.postValue(newPost);

                        } catch (JSONException e) {
                            errorLiveData.postValue("AI 返回的数据格式错误");
                        }
                    } else {
                        errorLiveData.postValue("API 返回了空内容");
                    }
                } else {
                    errorLiveData.postValue("API 错误: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                isLoadingLiveData.postValue(false);
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }

    public void clearError() {
        errorLiveData.setValue(null);
    }

    public void addFollowedPersona(Persona persona) {
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        
        boolean alreadyFollowed = false;
        for (Persona p : currentList) {
            if (p.getName().equals(persona.getName())) {
                alreadyFollowed = true;
                break;
            }
        }
        
        if (!alreadyFollowed) {
            currentList.add(persona);
            followedPersonasLiveData.setValue(currentList);
        }
    }

    public void removeFollowedPersona(Persona persona) {
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList != null) {
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).getName().equals(persona.getName())) {
                    currentList.remove(i);
                    followedPersonasLiveData.setValue(currentList);
                    break;
                }
            }
        }
    }

    @Deprecated
    public void updateFollowedPersonasFromSocialAdapter() {
    }
}