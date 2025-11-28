package com.example.demo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.Persona;
import com.example.demo.network.ApiClient;
import com.example.demo.network.ApiService;
import com.example.demo.network.ChatRequestMessage;
import com.example.demo.network.ChatRequest;
import com.example.demo.network.ChatResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.example.demo.BuildConfig;
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

    // API密钥和模型名称从BuildConfig获取
    // BuildConfig中的值从gradle.properties注入

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
    
    // 用户创建的Persona列表
    private final MutableLiveData<List<Persona>> userPersonasLiveData = new MutableLiveData<>(new ArrayList<>());
    // 当前用户正在使用的Persona
    private final MutableLiveData<Persona> currentUserPersonaLiveData = new MutableLiveData<>(null);
    // 用于快速查找Persona的名称集合
    private final Set<String> personaNameSet = new HashSet<>();

    /**
     * 私有构造函数，防止外部实例化
     * 初始化API服务实例
     */
    private UserPersonaRepository() {
        this.apiService = ApiClient.getApiService();
    }

    /**
     * 获取单例实例
     * @return MyPersonaRepository的单例实例
     */
    public static synchronized UserPersonaRepository getInstance() {
        if (instance == null) {
            instance = new UserPersonaRepository();
        }
        return instance;
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
     * 获取用户创建的Persona列表LiveData
     * @return 用户Persona列表的LiveData对象
     */
    public LiveData<List<Persona>> getUserPersonas() {
        return userPersonasLiveData;
    }
    
    /**
     * 获取当前选中的Persona LiveData
     * @return 当前选中Persona的LiveData对象
     */
    public LiveData<Persona> getCurrentUserPersona() {
        return currentUserPersonaLiveData;
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
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
                " (这是一个新的请求, 编号: " + randomNumber + ")" +
                "确保每次生成的人设名称和背景故事都是唯一的，而且每次生成的名字的第一个字都不同。";

        // 构建API请求历史
        List<ChatRequestMessage> apiHistory = new ArrayList<>();
        apiHistory.add(new ChatRequestMessage("system", systemPrompt));
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
    
    /**
     * 添加新的Persona到用户列表
     * @param persona 要添加的Persona
     * @return 如果成功添加返回true，如果名称已存在则返回false
     */
    public boolean addUserPersona(Persona persona) {
        if (persona == null || persona.getName() == null || persona.getName().trim().isEmpty()) {
            return false;
        }
        
        String personaName = persona.getName();
        
        // 检查是否已经存在同名Persona
        if (personaNameSet.contains(personaName)) {
            return false;
        }
        
        // 添加到集合和列表
        personaNameSet.add(personaName);
        List<Persona> currentList = new ArrayList<>(userPersonasLiveData.getValue());
        currentList.add(persona);
        userPersonasLiveData.setValue(currentList);
        
        // 如果这是第一个Persona，自动设为当前Persona
        if (currentList.size() == 1) {
            currentUserPersonaLiveData.setValue(persona);
        }
        
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
        
        String personaName = persona.getName();
        
        // 检查是否存在
        if (!personaNameSet.contains(personaName)) {
            return false;
        }
        
        // 从集合和列表中移除
        personaNameSet.remove(personaName);
        List<Persona> currentList = new ArrayList<>(userPersonasLiveData.getValue());
        currentList.removeIf(p -> p.getName().equals(personaName));
        userPersonasLiveData.setValue(currentList);
        
        // 如果删除的是当前Persona，需要重新选择
        Persona currentPersona = currentUserPersonaLiveData.getValue();
        if (currentPersona != null && currentPersona.getName().equals(personaName)) {
            // 如果还有其他Persona，选择第一个；否则设为null
            if (!currentList.isEmpty()) {
                currentUserPersonaLiveData.setValue(currentList.get(0));
            } else {
                currentUserPersonaLiveData.setValue(null);
            }
        }
        
        return true;
    }
    
    /**
     * 设置当前选中的Persona
     * @param persona 要设为当前的Persona
     * @return 如果成功设置返回true，如果Persona不存在于用户列表中则返回false
     */
    public boolean setCurrentUserPersona(Persona persona) {
        if (persona == null || persona.getName() == null) {
            return false;
        }
        
        String personaName = persona.getName();
        
        // 检查Persona是否存在于用户列表中
        if (!personaNameSet.contains(personaName)) {
            return false;
        }
        
        currentUserPersonaLiveData.setValue(persona);
        return true;
    }
    
    /**
     * 根据名称获取用户Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象，如果未找到则返回null
     */
    public Persona getUserPersonaByName(String name) {
        if (name == null) {
            return null;
        }
        
        List<Persona> personas = userPersonasLiveData.getValue();
        if (personas != null) {
            for (Persona persona : personas) {
                if (persona.getName().equals(name)) {
                    return persona;
                }
            }
        }
        return null;
    }
    
    /**
     * 检查是否有当前用户Persona
     * @return 如果有当前用户Persona返回true，否则返回false
     */
    public boolean hasCurrentUserPersona() {
        return currentUserPersonaLiveData.getValue() != null;
    }
    
    /**
     * 清除当前用户Persona
     */
    public void clearCurrentUserPersona() {
        currentUserPersonaLiveData.setValue(null);
    }
}