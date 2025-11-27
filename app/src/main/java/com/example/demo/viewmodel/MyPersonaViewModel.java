package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.model.Post;
import com.example.demo.repository.MyPersonaPostRepository;
import com.example.demo.repository.MyPersonaRepository;

import java.util.List;

/**
 * 我的Persona ViewModel类
 * 负责管理Persona和Post相关的数据和操作
 * 作为MyPersonaRepository的统一入口，符合MVVM架构原则
 * 使用LiveData观察数据变化，通知UI更新
 */
public class MyPersonaViewModel extends ViewModel {

    // Post数据仓库
    private final MyPersonaPostRepository myPersonaPostRepository;
    
    // Persona数据仓库
    private final MyPersonaRepository myPersonaRepository;

    /**
     * 构造函数
     * 初始化PostRepository和PersonaRepository实例
     */
    public MyPersonaViewModel() {
        this.myPersonaPostRepository = MyPersonaPostRepository.getInstance();
        this.myPersonaRepository = MyPersonaRepository.getInstance();
    }

    // ========== Post相关方法 ==========
    
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
     * 获取我的历史帖子LiveData
     * @return 我的帖子列表的LiveData对象
     */
    public LiveData<List<Post>> getMyPostsLiveData() {
        return myPersonaPostRepository.getMyPostsLiveData();
    }

    /**
     * 生成新动态
     * 调用PostRepository生成基于用户角色的社交媒体动态
     */
    public void generateNewPost() {
        // 从MyPersonaRepository获取当前用户Persona
        Persona currentUser = myPersonaRepository.getCurrentUserPersona().getValue();
        // 如果当前用户为空，设置错误信息并返回
        if (currentUser == null) {
            myPersonaPostRepository.setError("请先在 '我的 Persona' 中创建一个人设");
            return;
        }else{
            // 调用PostRepository生成新动态
            myPersonaPostRepository.generateNewPost(currentUser);
        }
    }
    
    // ========== Persona相关方法 ==========
    
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
     * 获取Persona加载状态LiveData
     * @return Persona加载状态的LiveData对象
     */
    public LiveData<Boolean> getPersonaIsLoading() {
        return myPersonaRepository.getIsLoading();
    }

    /**
     * 获取Persona错误信息LiveData
     * @return Persona错误信息的LiveData对象
     */
    public LiveData<String> getPersonaError() {
        return myPersonaRepository.getError();
    }

    /**
     * 清除Persona错误信息
     */
    public void clearPersonaError() {
        myPersonaRepository.clearError();
    }
    
    /**
     * 获取用户创建的Persona列表LiveData
     * @return 用户Persona列表的LiveData对象
     */
    public LiveData<List<Persona>> getUserPersonas() {
        return myPersonaRepository.getUserPersonas();
    }
    
    /**
     * 获取当前用户正在使用的Persona LiveData
     * @return 当前用户正在使用的Persona的LiveData对象
     */
    public LiveData<Persona> getCurrentUserPersona() {
        return myPersonaRepository.getCurrentUserPersona();
    }
    
    /**
     * 生成角色详情
     * 调用PersonaRepository生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        myPersonaRepository.generatePersonaDetails();
    }
    
    /**
     * 添加新的Persona到用户列表
     * @param persona 要添加的Persona
     * @return 如果成功添加返回true，如果名称已存在则返回false
     */
    public boolean addUserPersona(Persona persona) {
        return myPersonaRepository.addUserPersona(persona);
    }
    
    /**
     * 删除用户Persona
     * @param persona 要删除的Persona
     * @return 如果成功删除返回true，如果不存在则返回false
     */
    public boolean removeUserPersona(Persona persona) {
        return myPersonaRepository.removeUserPersona(persona);
    }
    
    /**
     * 设置当前选中的Persona
     * @param persona 要设为当前的Persona
     * @return 如果成功设置返回true，如果Persona不存在于用户列表中则返回false
     */
    public boolean setCurrentUserPersona(Persona persona) {
        return myPersonaRepository.setCurrentUserPersona(persona);
    }
    
    /**
     * 根据名称获取用户Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象，如果未找到则返回null
     */
    public Persona getUserPersonaByName(String name) {
        return myPersonaRepository.getUserPersonaByName(name);
    }
    
    /**
     * 检查是否有当前用户Persona
     * @return 如果有当前用户Persona返回true，否则返回false
     */
    public boolean hasCurrentUserPersona() {
        return myPersonaRepository.hasCurrentUserPersona();
    }
    
    /**
     * 清除当前用户Persona
     */
    public void clearCurrentUserPersona() {
        myPersonaRepository.clearCurrentUserPersona();
    }
}