package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;
import com.example.demo.repository.UserFollowedPersonaRepository;

import java.util.List;

/**
 * 关注角色ViewModel类
 * 负责管理关注列表
 * 使用LiveData观察数据变化，通知UI更新
 * 通过Repository模式管理数据，确保数据一致性
 */
public class UserFollowedListViewModel extends ViewModel {

    // 关注角色数据仓库
    private final UserFollowedPersonaRepository userFollowedPersonaRepository;
    
    // LiveData对象，用于观察错误消息
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     * 初始化Repository实例
     */
    public UserFollowedListViewModel() {
        userFollowedPersonaRepository = UserFollowedPersonaRepository.getInstance();
    }

    /**
     * 获取关注角色列表LiveData
     * @return 关注角色列表的LiveData对象
     */
    public LiveData<List<Persona>> getFollowedPersonas() {
        return userFollowedPersonaRepository.getFollowedPersonas();
    }
    
    /**
     * 获取错误消息LiveData
     * @return 错误消息的LiveData对象
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    /**
     * 清除错误消息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }

    /**
     * 添加关注角色
     * @param persona 要关注的角色
     * @return 如果成功添加返回true，如果已关注则返回false
     */
    public boolean addFollowedPersona(Persona persona) {
        return userFollowedPersonaRepository.addFollowedPersona(persona);
    }

    /**
     * 移除关注角色
     * @param persona 要移除的角色
     * @return 如果成功移除返回true，如果未关注则返回false
     */
    public boolean removeFollowedPersona(Persona persona) {
        return userFollowedPersonaRepository.removeFollowedPersona(persona);
    }

    /**
     * 检查是否已关注指定角色
     * @param persona 要检查的角色
     * @return 如果已关注返回true，否则返回false
     */
    public boolean isFollowingPersona(Persona persona) {
        return userFollowedPersonaRepository.isFollowingPersona(persona);
    }
    
    /**
     * 根据名称检查是否已关注
     * @param personaName 要检查的角色名称
     * @return 如果已关注返回true，否则返回false
     */
    public boolean isFollowingPersonaByName(String personaName) {
        return userFollowedPersonaRepository.isFollowingPersonaByName(personaName);
    }
}