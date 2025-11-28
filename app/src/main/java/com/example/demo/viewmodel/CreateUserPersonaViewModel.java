package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;

/**
 * 创建角色ViewModel类
 * 管理角色创建相关的数据和操作
 * 使用MyPersonaPostViewModel处理API调用和数据管理
 * 使用LiveData观察数据变化，通知UI更新
 */
public class CreateUserPersonaViewModel extends ViewModel {

    // 我的Persona和Post ViewModel
    private final UserPersonaCreateAndChatViewModel userPersonaCreateAndChatViewModel;

    /**
     * 构造函数
     * 初始化MyPersonaPostViewModel实例
     */
    public CreateUserPersonaViewModel() {
        this.userPersonaCreateAndChatViewModel = new UserPersonaCreateAndChatViewModel();
    }

    /**
     * 获取生成的角色名称LiveData
     * @return 角色名称的LiveData对象
     */
    public LiveData<String> getGeneratedName() {
        return userPersonaCreateAndChatViewModel.getGeneratedName();
    }

    /**
     * 获取生成的角色故事LiveData
     * @return 角色故事的LiveData对象
     */
    public LiveData<String> getGeneratedStory() {
        return userPersonaCreateAndChatViewModel.getGeneratedStory();
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return userPersonaCreateAndChatViewModel.getPersonaIsLoading();
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return userPersonaCreateAndChatViewModel.getPersonaError();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        userPersonaCreateAndChatViewModel.clearPersonaError();
    }

    /**
     * 生成角色详情
     * 调用MyPersonaPostViewModel生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        userPersonaCreateAndChatViewModel.generatePersonaDetails();
    }

    /**
     * 创建并保存Persona
     * @param name 角色名称
     * @param avatarDrawableId 头像资源ID
     * @param bio 个人简介
     * @param backgroundStory 背景故事
     * @return 创建的Persona对象
     */
    public Persona createPersona(String name, int avatarDrawableId, String bio, String backgroundStory) {
        Persona newPersona = new Persona(name, avatarDrawableId, bio, backgroundStory);
        
        // 通过MyPersonaPostViewModel将创建的Persona添加到Repository
        userPersonaCreateAndChatViewModel.addUserPersona(newPersona);
        
        return newPersona;
    }
}