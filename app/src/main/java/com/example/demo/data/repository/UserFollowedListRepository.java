package com.example.demo.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demo.model.Persona;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 关注角色数据仓库类
 * 负责管理和提供用户关注的Persona数据
 * 实现Repository模式，作为关注数据的单一数据源
 */
public class UserFollowedListRepository {

    // 单例实例
    private static UserFollowedListRepository instance;

    // 关注的Persona列表LiveData，用于观察数据变化
    private final MutableLiveData<List<Persona>> followedPersonasLiveData = new MutableLiveData<>(new ArrayList<>());
    
    // 关注的Persona ID集合，用于快速查找是否已关注
    private final Set<Long> followedPersonaIds = new HashSet<>();
    
    /**
     * 私有构造函数，实现单例模式
     */
    private UserFollowedListRepository() {
        // 初始化时可以为空，或者从持久化存储加载数据
    }
    
    /**
     * 获取Repository的单例实例
     * @return FollowedPersonaRepository实例
     */
    public static synchronized UserFollowedListRepository getInstance() {
        if (instance == null) {
            instance = new UserFollowedListRepository();
        }
        return instance;
    }

    /**
     * 获取关注角色列表的LiveData
     * @return 可观察的关注角色列表LiveData
     */
    public LiveData<List<Persona>> getFollowedPersonas() {
        return followedPersonasLiveData;
    }
    
    /**
     * 添加关注角色
     * @param persona 要关注的角色
     * @return 如果成功添加返回true，如果已关注则返回false
     */
    public boolean addFollowedPersona(Persona persona) {
        if (persona == null) {
            return false;
        }
        
        long personaId = persona.getId();
        
        // 检查是否已经关注
        if (followedPersonaIds.contains(personaId)) {
            return false;
        }
        
        // 添加到集合和列表
        followedPersonaIds.add(personaId);
        List<Persona> currentList = followedPersonasLiveData.getValue();
        // 创建新列表，复制当前列表内容
        List<Persona> newList = new ArrayList<>();
        if (currentList != null) {
            newList.addAll(currentList);
        }
        newList.add(persona);
        followedPersonasLiveData.setValue(newList);
        
        return true;
    }
    
    /**
     * 移除关注角色
     * @param persona 要移除的角色
     * @return 如果成功移除返回true，如果未关注则返回false
     */
    public boolean removeFollowedPersona(Persona persona) {
        if (persona == null) {
            return false;
        }
        
        long personaId = persona.getId();
        
        // 检查是否已关注
        if (!followedPersonaIds.contains(personaId)) {
            return false;
        }
        
        // 从集合和列表中移除
        followedPersonaIds.remove(personaId);
        List<Persona> currentList = followedPersonasLiveData.getValue();
        // 创建新列表，复制当前列表内容
        List<Persona> newList = new ArrayList<>();
        if (currentList != null) {
            newList.addAll(currentList);
            // 遍历新列表，移除对应的Persona
            for (int i = 0; i < newList.size(); i++) {
                if (newList.get(i).getId() == personaId) {
                    newList.remove(i);
                    break;
                }
            }
        }
        followedPersonasLiveData.setValue(newList);
        
        return true;
    }
}