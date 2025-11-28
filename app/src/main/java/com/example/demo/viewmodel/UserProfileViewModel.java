package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * 个人资料ViewModel
 * 负责管理个人资料页面的所有数据和业务逻辑
 * 处理我的Persona、应用设置、关于和退出登录等功能
 */
public class UserProfileViewModel extends ViewModel {

    // 加载状态LiveData
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    
    // 错误信息LiveData
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * 构造函数
     */
    public UserProfileViewModel() {
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态LiveData
     */
    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息LiveData
     */
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    /**
     * 处理"我的Persona"点击事件
     */
    public void onMyPersonasClick() {
        // 暂未实现具体逻辑
    }

    /**
     * 处理"应用设置"点击事件
     */
    public void onAppSettingsClick() {
        // 暂未实现具体逻辑
    }

    /**
     * 处理"关于"点击事件
     */
    public void onAboutClick() {
        // 暂未实现具体逻辑
    }

    /**
     * 处理"退出登录"点击事件
     */
    public void onLogOutClick() {
        // 暂未实现具体逻辑
    }

    /**
     * 清除错误信息
     */
    public void clearError() {
        errorLiveData.setValue(null);
    }
}