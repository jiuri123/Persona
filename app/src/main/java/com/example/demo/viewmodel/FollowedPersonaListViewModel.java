package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注角色ViewModel类
 * 负责管理关注列表
 * 使用LiveData观察数据变化，通知UI更新
 */
public class FollowedPersonaListViewModel extends ViewModel {

    // LiveData对象，用于观察关注列表变化
    private final MutableLiveData<List<Persona>> followedPersonasLiveData = new MutableLiveData<>(new ArrayList<>());
    
    // LiveData对象，用于观察错误消息
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 获取关注角色列表LiveData
     * @return 关注角色列表的LiveData对象
     */
    public LiveData<List<Persona>> getFollowedPersonas() {
        return followedPersonasLiveData;
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
     */
    public void addFollowedPersona(Persona persona) {
        // 获取当前关注列表
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList == null) {
            currentList = new ArrayList<>();
        }
        
        // 检查是否已经关注
        boolean alreadyFollowed = false;
        for (Persona p : currentList) {
            if (p.getName().equals(persona.getName())) {
                alreadyFollowed = true;
                break;
            }
        }
        
        // 如果未关注，则添加到列表
        if (!alreadyFollowed) {
            currentList.add(persona);
            followedPersonasLiveData.setValue(currentList);
        }
    }

    /**
     * 移除关注角色
     * @param persona 要移除的角色
     */
    public void removeFollowedPersona(Persona persona) {
        // 获取当前关注列表
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList != null) {
            // 查找并移除指定角色
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).getName().equals(persona.getName())) {
                    currentList.remove(i);
                    followedPersonasLiveData.setValue(currentList);
                    break;
                }
            }
        }
    }

    /**
     * 检查是否已关注指定角色
     * @param persona 要检查的角色
     * @return 如果已关注返回true，否则返回false
     */
    public boolean isFollowingPersona(Persona persona) {
        List<Persona> currentList = followedPersonasLiveData.getValue();
        if (currentList == null) {
            return false;
        }
        
        for (Persona p : currentList) {
            if (p.getName().equals(persona.getName())) {
                return true;
            }
        }
        return false;
    }
}