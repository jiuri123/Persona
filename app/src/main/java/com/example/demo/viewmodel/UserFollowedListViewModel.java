package com.example.demo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.demo.model.OtherPersona;
import com.example.demo.data.repository.UserFollowedListRepository;

import java.util.List;

/**
 * 关注角色ViewModel类
 * 负责管理关注列表
 * 使用LiveData观察数据变化，通知UI更新
 * 通过Repository模式管理数据，确保数据一致性
 */
public class UserFollowedListViewModel extends ViewModel {

    // 关注角色数据仓库
    private final UserFollowedListRepository userFollowedListRepository;
    
    // LiveData对象，用于观察错误消息
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    
    // 使用MediatorLiveData包装关注角色列表
    private final MediatorLiveData<List<OtherPersona>> followedPersonasLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化Repository实例
     */
    public UserFollowedListViewModel(Application application) {
        userFollowedListRepository = UserFollowedListRepository.getInstance(application);
        // 观察关注角色列表变化
        followedPersonasLiveData.addSource(userFollowedListRepository.getFollowedPersonas(), followedPersonasLiveData::setValue);
    }
    
    /**
     * 获取关注角色列表LiveData
     * @return 关注角色列表的LiveData对象
     */
    public LiveData<List<OtherPersona>> getFollowedPersonas() {
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
     * 移除关注角色
     * @param otherPersona 要移除的角色
     * @return 如果成功移除返回true，如果未关注则返回false
     */
    public boolean removeFollowedPersona(OtherPersona otherPersona) {
        return userFollowedListRepository.removeFollowedPersona(otherPersona);
    }
    
    /**
     * ViewModelFactory，用于创建带有Application参数的ViewModel
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;

        public Factory(@NonNull Application application) {
            this.application = application;
        }

        @NonNull
        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(UserFollowedListViewModel.class)) {
                return (T) new UserFollowedListViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}