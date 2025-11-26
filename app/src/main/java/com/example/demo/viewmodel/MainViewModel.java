package com.example.demo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatMessageDto;
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
 * 共享的 ViewModel，用于在 MainActivity 下的各个 Fragment 之间通信。
 * 1. 持有用户创建的 Persona。
 * 2. 负责为该 Persona 生成社交动态。
 */
public class MainViewModel extends ViewModel {

    // --- 1. API 常量和 Service 实例 (从 CreatePersonaViewModel 复制) ---
    private static final String API_KEY = "Bearer sk-XCV331xFtjmzsMB4vB2P1dXjD3HLuqDwsOHigF1Ray0o9t8L"; //
    private static final String MODEL_NAME = "moonshot-v1-8k"; //
    private final ApiService apiService;
    private final Random random = new Random();

    // --- 2. 共享数据 LiveData ---

    // (A) 持有用户创建的 Persona
    private final MutableLiveData<Persona> userPersonaLiveData = new MutableLiveData<>(null);

    // (B) 持有 AI *新生成* 的动态 (用于 SocialSquareFragment)
    private final MutableLiveData<PersonaPost> newPostLiveData = new MutableLiveData<>();

    // (C) 持有已关注的Persona列表 (用于 FollowedListFragment)
    private final MutableLiveData<List<Persona>> followedPersonasLiveData = new MutableLiveData<>(new ArrayList<>());

    // (D) 通用的加载和错误状态
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     */
    public MainViewModel() {
        this.apiService = ApiClient.getApiService(); //
    }

    // --- 3. Getters (供 Fragment 观察) ---

    public LiveData<PersonaPost> getNewPostLiveData() {
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

    // --- 4. 核心方法 (供 Fragment 调用) ---

    /**
     * [供 MyPersonaFragment 调用]
     * 当用户创建 Persona 成功时，调用此方法来"注册" Persona
     */
    public void setUserPersona(Persona persona) {
        userPersonaLiveData.setValue(persona);
    }

    /**
     * [供 SocialSquareFragment 调用]
     * 当用户点击 "发布" 按钮时，调用此方法
     */
    public void generateNewPost() {
        // 1. 检查 Persona 是否存在
        Persona currentUser = userPersonaLiveData.getValue();
        if (currentUser == null) {
            errorLiveData.setValue("请先在 '我的 Persona' 标签页中创建一个人设");
            return;
        }

        // 2. 检查是否已在加载
        if (Boolean.TRUE.equals(isLoadingLiveData.getValue())) {
            return; // 正在加载，防止重复点击
        }
        isLoadingLiveData.setValue(true);

        // --- 3. 准备 AI 指令 ---
        // a. 系统指令：强制 AI 返回 JSON
        String systemPrompt = "你是一个社交媒体动态生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"content\": \"[生成的动态正文，必须包含Markdown格式，如**粗体**、*斜体*、~~删除线~~、列表等]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";

        // b. 用户指令：基于 Persona 的人设来生成内容
        int randomNumber = random.nextInt(10000);
        // 随机选择语言：0为中文，1为英文
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

        // 4. 准备 API 请求
        List<ChatMessageDto> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatMessageDto("system", systemPrompt)); //
        apiHistory.add(new ChatMessageDto("user", userPrompt)); //
        ChatRequest request = new ChatRequest(MODEL_NAME, apiHistory); //

        // 5. 异步调用 API
        apiService.getChatCompletion(API_KEY, request).enqueue(new Callback<ChatResponse>() { //
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                isLoadingLiveData.postValue(false);
                if (response.isSuccessful() && response.body() != null) { //
                    String aiContent = response.body().getFirstMessageContent();
                    if (aiContent != null) {
                        try {
                            // a. 解析 AI 返回的 JSON 字符串
                            JSONObject jsonResponse = new JSONObject(aiContent);
                            String postContent = jsonResponse.getString("content");

                            // b. 创建 PersonaPost 对象
                            //    (我们暂时不生成图片，所以图片 ID 为 null)
                            PersonaPost newPost = new PersonaPost( //
                                    currentUser, // 作者就是我们自己的 Persona
                                    postContent,
                                    null, // 暂无 AI 配图
                                    "刚刚"
                            );

                            // c. 将新动态发送回 SocialSquareFragment
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

    /**
     * [!! 新增 !!]
     * 供 Fragment 调用，用于"消费"或"清除"错误消息
     * 防止同一个错误被 Toast 多次（或不被 Toast）
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }

    /**
     * 添加Persona到已关注列表
     */
    public void addFollowedPersona(Persona persona) {
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        
        // 检查是否已经关注
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

    /**
     * 从已关注列表中移除Persona
     */
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

    /**
     * 从SocialSquareAdapter获取已关注的Persona列表
     * 这个方法现在不再需要，因为关注列表通过addFollowedPersona和removeFollowedPersona方法管理
     * 保留此方法仅为兼容性
     */
    @Deprecated
    public void updateFollowedPersonasFromSocialAdapter() {
        // 不再需要实现，因为关注列表现在通过addFollowedPersona和removeFollowedPersona方法管理
    }
}