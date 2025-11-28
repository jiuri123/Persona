package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.repository.UserPersonaChatRepository;
import com.example.demo.repository.UserPersonaRepository;

import java.util.List;

/**
 * 我的Persona ViewModel类
 * 负责管理Persona相关的数据和操作
 * 作为UserPersonaRepository的统一入口，符合MVVM架构原则
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaCreateAndChatViewModel extends ViewModel {

    // Persona数据仓库
    private final UserPersonaRepository userPersonaRepository;
    
    // 用户自己创建的Persona聊天仓库
    private final UserPersonaChatRepository userPersonaChatRepository;
    
    // 使用MediatorLiveData包装所有Repository的LiveData
    private final MediatorLiveData<String> generatedNameLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> generatedStoryLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Boolean> personaIsLoadingLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> personaErrorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Persona>> userPersonasLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Persona> currentUserPersonaLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<ChatMessage>> chatHistoryLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化PersonaRepository实例
     */
    public UserPersonaCreateAndChatViewModel() {
        this.userPersonaRepository = UserPersonaRepository.getInstance();
        this.userPersonaChatRepository = UserPersonaChatRepository.getInstance();
        setupMediatorLiveData();
    }

    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // Persona相关LiveData
        generatedNameLiveData.addSource(userPersonaRepository.getGeneratedName(), generatedNameLiveData::setValue);
        generatedStoryLiveData.addSource(userPersonaRepository.getGeneratedStory(), generatedStoryLiveData::setValue);
        personaIsLoadingLiveData.addSource(userPersonaRepository.getIsLoading(), personaIsLoadingLiveData::setValue);
        personaErrorLiveData.addSource(userPersonaRepository.getError(), personaErrorLiveData::setValue);
        userPersonasLiveData.addSource(userPersonaRepository.getUserPersonas(), userPersonasLiveData::setValue);
        currentUserPersonaLiveData.addSource(userPersonaRepository.getCurrentUserPersona(), currentUserPersonaLiveData::setValue);
        
        // 聊天相关LiveData
        chatHistoryLiveData.addSource(userPersonaChatRepository.getChatHistory(), chatHistoryLiveData::setValue);
    }
    
    // ========== Persona相关方法 ==========
    
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
     * 获取Persona加载状态LiveData
     * @return Persona加载状态的LiveData对象
     */
    public LiveData<Boolean> getPersonaIsLoading() {
        return personaIsLoadingLiveData;
    }

    /**
     * 获取Persona错误信息LiveData
     * @return Persona错误信息的LiveData对象
     */
    public LiveData<String> getPersonaError() {
        return personaErrorLiveData;
    }

    /**
     * 清除Persona错误信息
     */
    public void clearPersonaError() {
        userPersonaRepository.clearError();
    }
    
    /**
     * 获取用户创建的Persona列表LiveData
     * @return 用户Persona列表的LiveData对象
     */
    public LiveData<List<Persona>> getUserPersonas() {
        return userPersonasLiveData;
    }
    
    /**
     * 获取当前用户正在使用的Persona LiveData
     * @return 当前用户正在使用的Persona的LiveData对象
     */
    public LiveData<Persona> getCurrentUserPersona() {
        return currentUserPersonaLiveData;
    }
    
    /**
     * 生成角色详情
     * 调用PersonaRepository生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        userPersonaRepository.generatePersonaDetails();
    }
    
    /**
     * 添加新的Persona到用户列表
     * @param persona 要添加的Persona
     * @return 如果成功添加返回true，如果名称已存在则返回false
     */
    public boolean addUserPersona(Persona persona) {
        return userPersonaRepository.addUserPersona(persona);
    }
    
    /**
     * 删除用户Persona
     * @param persona 要删除的Persona
     * @return 如果成功删除返回true，如果不存在则返回false
     */
    public boolean removeUserPersona(Persona persona) {
        return userPersonaRepository.removeUserPersona(persona);
    }
    
    /**
     * 设置当前选中的Persona
     * @param persona 要设为当前的Persona
     * @return 如果成功设置返回true，如果Persona不存在于用户列表中则返回false
     */
    public boolean setCurrentUserPersona(Persona persona) {
        boolean result = userPersonaRepository.setCurrentUserPersona(persona);
        if (result) {
            // 设置当前聊天的Persona
            userPersonaChatRepository.setCurrentPersona(persona);
        }
        return result;
    }
    
    /**
     * 根据名称获取用户Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象，如果未找到则返回null
     */
    public Persona getUserPersonaByName(String name) {
        return userPersonaRepository.getUserPersonaByName(name);
    }
    
    /**
     * 检查是否有当前用户Persona
     * @return 如果有当前用户Persona返回true，否则返回false
     */
    public boolean hasCurrentUserPersona() {
        return userPersonaRepository.hasCurrentUserPersona();
    }
    
    /**
     * 清除当前用户Persona
     */
    public void clearCurrentUserPersona() {
        userPersonaRepository.clearCurrentUserPersona();
    }
    
    // ========== 聊天相关方法 ==========
    
    /**
     * 获取聊天历史LiveData
     * @return 聊天历史消息的LiveData对象，UI组件可以观察此数据变化
     */
    public LiveData<List<ChatMessage>> getChatHistory() {
        return chatHistoryLiveData;
    }
    
    /**
     * 发送消息
     * @param messageText 要发送的消息文本
     */
    public void sendMessage(String messageText) {
        userPersonaChatRepository.sendMessage(messageText);
    }
}