package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.data.repository.UserPersonaChatRepository;

import java.util.List;

/**
 * 用户创建的Persona聊天ViewModel类
 * 负责管理与用户创建的Persona的聊天相关数据和操作
 * 作为UserPersonaChatRepository的统一入口，符合MVVM架构原则
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaChatViewModel extends AndroidViewModel {

    // 用户自己创建的Persona聊天仓库
    private final UserPersonaChatRepository userPersonaChatRepository;
    
    // 使用MediatorLiveData包装Repository的LiveData
    private final MediatorLiveData<List<ChatMessage>> chatHistoryLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化UserPersonaChatRepository实例
     * @param application Application实例
     */
    public UserPersonaChatViewModel(Application application) {
        super(application);
        this.userPersonaChatRepository = UserPersonaChatRepository.getInstance();
        setupMediatorLiveData();
    }

    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // 聊天相关LiveData
        chatHistoryLiveData.addSource(userPersonaChatRepository.getChatHistory(), chatHistoryLiveData::setValue);
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

    /**
     * 设置当前聊天的Persona
     * @param currentPersona 要设置的Persona对象
     */
    public void setCurrentPersona(Persona currentPersona) {
        userPersonaChatRepository.setCurrentPersona(currentPersona);
    }
    
    /**
     * 重置聊天历史
     */
    public void resetChatHistory() {
        userPersonaChatRepository.resetChatHistory();
    }
}