package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.repository.MyPersonaRepository;

/**
 * 创建角色ViewModel类
 * 管理角色创建相关的数据和操作
 * 使用PersonaRepository处理API调用
 * 使用LiveData观察数据变化，通知UI更新
 */
public class CreateMyPersonaViewModel extends ViewModel {

    // Persona数据仓库
    private final MyPersonaRepository myPersonaRepository;

    /**
     * 构造函数
     * 初始化PersonaRepository实例
     */
    public CreateMyPersonaViewModel() {
        this.myPersonaRepository = MyPersonaRepository.getInstance();
    }

    /**
     * 获取生成的角色名称LiveData
     * @return 角色名称的LiveData对象
     */
    public LiveData<String> getGeneratedName() {
        return myPersonaRepository.getGeneratedName();
    }

    /**
     * 获取生成的角色故事LiveData
     * @return 角色故事的LiveData对象
     */
    public LiveData<String> getGeneratedStory() {
        return myPersonaRepository.getGeneratedStory();
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return myPersonaRepository.getIsLoading();
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return myPersonaRepository.getError();
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        myPersonaRepository.clearError();
    }

    /**
     * 生成角色详情
     * 调用PersonaRepository生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        myPersonaRepository.generatePersonaDetails();
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
        
        // 将创建的Persona设置为当前用户Persona
        SharedViewModel.getInstance().setCurrentUserPersona(newPersona);
        
        return newPersona;
    }
}