package com.example.demo.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.Persona;
import com.example.demo.repository.OtherPersonaChatRepository;

import java.util.List;

/**
 * 其他Persona聊天ViewModel类
 * 管理与其他Persona相关的聊天数据和操作
 * 遵循MVVM架构模式，负责UI与数据之间的交互
 */
public class OtherPersonaChatViewModel extends ViewModel {

    private OtherPersonaChatRepository chatRepository;

    private LiveData<List<ChatMessage>> chatHistoryLiveData;

    /**
     * 初始化ViewModel
     * @param persona 关联的角色对象
     */
    public void init(Persona persona) {
        if (chatRepository == null) {
            chatRepository = new OtherPersonaChatRepository(persona);
            chatHistoryLiveData = chatRepository.getChatHistory();
        }
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
        if (chatRepository != null) {
            chatRepository.sendMessage(messageText);
        }
    }

    /**
     * ViewModel工厂类
     * 用于创建带有参数的OtherPersonaChatViewModel实例
     * 实现ViewModelProvider.NewInstanceFactory接口
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Persona persona;

        /**
         * 构造函数
         * @param persona 要传递给ViewModel的角色对象
         */
        public Factory(Persona persona) {
            this.persona = persona;
        }

        /**
         * 创建ViewModel实例
         * @param modelClass ViewModel类
         * @return 创建的ViewModel实例
         */
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            OtherPersonaChatViewModel viewModel = new OtherPersonaChatViewModel();
            viewModel.init(persona);
            return (T) viewModel;
        }
    }
}