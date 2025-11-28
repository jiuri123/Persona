package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.repository.UserPersonaRepository;

/**
 * 创建角色ViewModel类
 * 管理角色创建相关的数据和操作
 * 直接使用UserPersonaRepository处理API调用和数据管理
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaCreatingViewModel extends ViewModel {

    // Persona数据仓库
    private final UserPersonaRepository userPersonaRepository;

    /**
     * 构造函数
     * 初始化UserPersonaRepository实例
     */
    public UserPersonaCreatingViewModel() {
        this.userPersonaRepository = UserPersonaRepository.getInstance();
    }

    /**
     * 获取生成的角色名称LiveData
     * @return 角色名称的LiveData对象
     */
    public LiveData<String> getGeneratedName() {
        return userPersonaRepository.getGeneratedName();
    }

    /**
     * 获取生成的角色故事LiveData
     * @return 角色故事的LiveData对象
     */
    public LiveData<String> getGeneratedStory() {
        return userPersonaRepository.getGeneratedStory();
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return userPersonaRepository.getIsLoading();
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return userPersonaRepository.getError();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        userPersonaRepository.clearError();
    }

    /**
     * 生成角色详情
     * 调用UserPersonaRepository生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        userPersonaRepository.generatePersonaDetails();
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
        
        // 直接通过UserPersonaRepository将创建的Persona添加到仓库
        userPersonaRepository.addUserPersona(newPersona);
        
        return newPersona;
    }
}