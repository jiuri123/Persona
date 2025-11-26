package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;

/**
 * 共享ViewModel类
 * 管理全局状态，如当前用户的Persona
 * 使用单例模式确保全局只有一个实例
 * 使用LiveData观察数据变化，通知UI更新
 */
public class SharedViewModel extends ViewModel {

    // 单例实例
    private static SharedViewModel instance;

    // LiveData对象，用于观察用户角色变化
    private final MutableLiveData<Persona> currentUserPersonaLiveData = new MutableLiveData<>(null);

    /**
     * 私有构造函数，防止外部实例化
     */
    private SharedViewModel() {
        // 私有构造函数
    }

    /**
     * 获取单例实例
     * @return SharedViewModel的单例实例
     */
    public static synchronized SharedViewModel getInstance() {
        if (instance == null) {
            instance = new SharedViewModel();
        }
        return instance;
    }

    /**
     * 获取当前用户角色LiveData
     * @return 当前用户角色的LiveData对象
     */
    public LiveData<Persona> getCurrentUserPersona() {
        return currentUserPersonaLiveData;
    }

    /**
     * 设置当前用户角色
     * @param persona 用户角色对象
     */
    public void setCurrentUserPersona(Persona persona) {
        currentUserPersonaLiveData.setValue(persona);
    }

    /**
     * 清除当前用户角色
     */
    public void clearCurrentUserPersona() {
        currentUserPersonaLiveData.setValue(null);
    }

    /**
     * 检查是否有当前用户角色
     * @return 如果有当前用户角色返回true，否则返回false
     */
    public boolean hasCurrentUserPersona() {
        return currentUserPersonaLiveData.getValue() != null;
    }
}