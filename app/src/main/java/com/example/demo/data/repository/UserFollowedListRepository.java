package com.example.demo.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.demo.data.local.LocalDataSource;
import com.example.demo.model.OtherPersona;

import java.util.List;

/**
 * 关注角色数据仓库类
 * 负责管理和提供用户关注的Persona数据
 * 实现Repository模式，作为关注数据的单一数据源
 */
public class UserFollowedListRepository {

    // 单例实例
    private static UserFollowedListRepository instance;

    // 本地数据源实例
    private final LocalDataSource localDataSource;
    
    /**
     * 私有构造函数，实现单例模式
     */
    private UserFollowedListRepository(Context context) {
        // 初始化本地数据源
        this.localDataSource = LocalDataSource.getInstance(context);
    }
    
    /**
     * 获取Repository的单例实例
     * @param context 上下文
     * @return FollowedPersonaRepository实例
     */
    public static synchronized UserFollowedListRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserFollowedListRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 获取关注角色列表的LiveData
     * @return 可观察的关注角色列表LiveData
     */
    public LiveData<List<OtherPersona>> getFollowedPersonas() {
        return localDataSource.getAllOtherPersonasOrderByCreatedAtDesc();
    }
    
    /**
     * 添加关注角色
     * @param otherPersona 要关注的角色
     * @return 如果成功添加返回true，如果已关注则返回false
     */
    public boolean addFollowedPersona(OtherPersona otherPersona) {
        if (otherPersona == null) {
            return false;
        }

        // 通过本地数据源插入数据
        localDataSource.insertOtherPersona(otherPersona);
        
        return true;
    }
    
    /**
     * 移除关注角色
     * @param otherPersona 要移除的角色
     * @return 如果成功移除返回true，如果未关注则返回false
     */
    public boolean removeFollowedPersona(OtherPersona otherPersona) {
        if (otherPersona == null) {
            return false;
        }
        
        // 通过本地数据源删除数据
        localDataSource.deleteOtherPersona(otherPersona);
        
        return true;
    }
}