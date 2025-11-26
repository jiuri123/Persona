package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.repository.MyPersonaPostRepository;

/**
 * 动态生成ViewModel类
 * 负责生成新动态
 * 使用PostRepository处理API调用
 * 使用LiveData观察数据变化，通知UI更新
 */
public class PostGenerationViewModel extends ViewModel {

    // Post数据仓库
    private final MyPersonaPostRepository myPersonaPostRepository;

    /**
     * 构造函数
     * 初始化PostRepository实例
     */
    public PostGenerationViewModel() {
        this.myPersonaPostRepository = new MyPersonaPostRepository();
    }

    /**
     * 获取新动态LiveData
     * @return 新动态的LiveData对象
     */
    public LiveData<Post> getNewPostLiveData() {
        return myPersonaPostRepository.getNewPostLiveData();
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return myPersonaPostRepository.getIsLoading();
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return myPersonaPostRepository.getError();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        myPersonaPostRepository.clearError();
    }

    /**
     * 生成新动态
     * 调用PostRepository生成基于用户角色的社交媒体动态
     */
    public void generateNewPost() {
        // 从SharedViewModel获取当前用户Persona
        Persona currentUser = SharedViewModel.getInstance().getCurrentUserPersona().getValue();
        myPersonaPostRepository.generateNewPost(currentUser);
    }
}