package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.repository.OtherPersonaChatRepository;
import com.example.demo.repository.UserPersonaChatRepository;
import com.example.demo.repository.UserPersonaRepository;

import java.util.List;

/**
 * 其他Persona聊天ViewModel类
 * 管理与其他Persona相关的聊天数据和操作
 * 遵循MVVM架构模式，负责UI与数据之间的交互
 */
public class OtherPersonaChatViewModel extends ViewModel {

    private final OtherPersonaChatRepository otherPersonaChatRepository;

    private final MediatorLiveData<List<ChatMessage>> chatHistoryLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化PersonaRepository实例
     */
    public OtherPersonaChatViewModel() {
        this.otherPersonaChatRepository = OtherPersonaChatRepository.getInstance();
        this.chatHistoryLiveData.addSource(otherPersonaChatRepository.getChatHistory(), chatHistoryLiveData::setValue);
    }

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
        if (otherPersonaChatRepository != null) {
            otherPersonaChatRepository.sendMessage(messageText);
        }
    }

    /**
     * 设置当前聊天的Persona
     * @param persona 要设置的Persona对象
     */
    public void setCurrentPersona(Persona persona) {
        if (otherPersonaChatRepository != null) {
            otherPersonaChatRepository.setCurrentPersona(persona);
        }
    }
}