package com.example.demo.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.data.local.LocalDataSource;
import com.example.demo.model.Persona;
import com.example.demo.data.remote.ApiClient;
import com.example.demo.data.remote.ApiService;
import com.example.demo.data.remote.model.ChatRequestMessage;
import com.example.demo.data.remote.model.ChatRequest;
import com.example.demo.data.remote.model.ChatResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.example.demo.BuildConfig;
import com.example.demo.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Persona数据仓库类
 * 负责管理与Persona相关的API调用和数据管理
 * 实现Repository模式，封装网络请求和数据管理逻辑
 * 使用单例模式确保全局只有一个实例
 */
public class UserPersonaRepository {

    // 单例实例
    private static UserPersonaRepository instance;

    // 网络服务和随机数生成器
    private final ApiService apiService;
    private final Random random = new Random();
    
    // 本地数据源
    private final LocalDataSource localDataSource;

    // 角色主题数组
    private static final String[] THEMES = {
            "赛博朋克", "奇幻", "科幻", "蒸汽朋克", "神秘", "历史", "艺术家", "探险家", "AI", "时间旅行者"
    };

    // 系统提示词，在构造函数中初始化
    private final String systemPrompt;

    // LiveData对象，用于观察数据变化
    private final MutableLiveData<Persona> generatedPersonaLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 私有构造函数，防止外部实例化
     * 初始化API服务实例和系统提示词
     * @param context 上下文
     */
    private UserPersonaRepository(Context context) {
        this.apiService = ApiClient.getApiService();
        
        // 初始化本地数据源（使用单例实例）
        this.localDataSource = LocalDataSource.getInstance(context);
        
        // 初始化系统提示词，只在构造函数中初始化一次
        this.systemPrompt = "你是一个富有创造力的人设生成器。" +
                "请你只返回一个 JSON 对象，格式如下：" +
                "{\"name\": \"[生成的人设名称]\", \"gender\": \"[生成的性别]\", \"personality\": \"[生成的性格]\", \"age\": [生成的年龄数字], \"relationship\": \"[生成的和我的关系，比如：情侣、父子、朋友、导师等]\", \"catchphrase\": \"[生成的口头禅]\", \"story\": \"[生成的背景故事，2-3句话]\"}" +
                "不要在 JSON 之外添加任何解释性文字。";
    }

    /**
     * 获取单例实例
     * @param context 上下文
     * @return UserPersonaRepository的单例实例
     */
    public static synchronized UserPersonaRepository getInstance(Context context) {
        if (instance == null) {
            // 保存上下文
            // 上下文
            Context appContext = context.getApplicationContext();
            instance = new UserPersonaRepository(appContext);
        }
        return instance;
    }

    /**
     * 生成角色详情
     * 调用AI API生成角色名称和背景故事
     * 使用随机主题增加角色多样性
     */
    public void generatePersonaDetails() {
        // 设置加载状态为true
        isLoadingLiveData.setValue(true);

        // 生成随机数和随机主题
        int randomNumber = random.nextInt(10000);
        String randomTheme = THEMES[random.nextInt(THEMES.length)];

        // 用户提示，包含随机主题
        String userPrompt = "请为我生成一个独特且有趣的 Persona 角色。" +
                "请让人设带有一点 [" + randomTheme + "] 风格。" +
                " (这是一个新的请求, 编号: " + randomNumber + ")" +
                "确保每次生成的人设名称、性别、性格、年龄、关系、口头禅、背景故事都是不同的，而且每次生成的名字的第一个字都不同。";

        // 构建API请求历史，使用预定义的systemPrompt变量
        List<ChatRequestMessage> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatRequestMessage("system", this.systemPrompt));
        apiHistory.add(new ChatRequestMessage("user", userPrompt));

        // 创建聊天请求
        ChatRequest request = new ChatRequest(BuildConfig.MODEL_NAME, apiHistory);

        // 异步调用API
        apiService.getChatCompletion(BuildConfig.API_KEY, request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
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
                            String gender = jsonResponse.getString("gender");
                            String personality = jsonResponse.getString("personality");
                            int age = jsonResponse.getInt("age");
                            String relationship = jsonResponse.getString("relationship");
                            String catchphrase = jsonResponse.getString("catchphrase");
                            String story = jsonResponse.getString("story");

                            // 创建Persona对象（使用默认头像，将catchphrase作为signature，id设为0由系统自动生成）
                            int avatarId = R.drawable.avatar_zero;
                            Persona generatedPersona = new Persona(
                                    0, name, avatarId, null, catchphrase, story,
                                    gender, age, personality, relationship
                            );

                            // 更新LiveData，通知UI更新
                            generatedPersonaLiveData.postValue(generatedPersona);

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
            public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                // 请求失败，设置加载状态为false
                isLoadingLiveData.postValue(false);
                // 网络错误
                errorLiveData.postValue("网络请求失败: " + t.getMessage());
            }
        });
    }

    /**
     * 获取生成的角色LiveData
     * @return 角色的LiveData对象
     */
    public LiveData<Persona> getGeneratedPersona() {
        return generatedPersonaLiveData;
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
     * 获取用户创建的Persona列表LiveData
     * @return 用户Persona列表的LiveData对象
     */
    public LiveData<List<Persona>> getUserPersonas() {
        return localDataSource.getAllPersonas();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }
    
    /**
     * 清除生成的Persona对象
     */
    public void clearGeneratedPersona() {
        generatedPersonaLiveData.setValue(null);
    }

    /**
     * 添加新的Persona到用户列表
     * @param persona 要添加的Persona
     * @return 如果成功添加返回true，如果名称已存在则返回false
     */
    public boolean addUserPersona(Persona persona) {
        // 添加到本地数据库
        localDataSource.insertPersona(persona);
        return true;
    }
    
    /**
     * 删除用户Persona
     * @param persona 要删除的Persona
     * @return 如果成功删除返回true，如果不存在则返回false
     */
    public boolean removeUserPersona(Persona persona) {
        if (persona == null || persona.getName() == null) {
            return false;
        }
        localDataSource.deletePersona(persona);

        return true;
    }
}