package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.demo.model.UserPersona;
import com.example.demo.data.repository.UserPersonaRepository;

import java.util.List;

/**
 * 我的Persona ViewModel类
 * 负责管理Persona相关的数据和操作
 * 作为UserPersonaRepository的统一入口，符合MVVM架构原则
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaViewModel extends AndroidViewModel {

    // Persona数据仓库
    private final UserPersonaRepository userPersonaRepository;
    private final MediatorLiveData<List<UserPersona>> userPersonasLiveData = new MediatorLiveData<>();

    /**
     * 构造函数
     * 初始化PersonaRepository实例
     * @param application Application实例
     */
    public UserPersonaViewModel(Application application) {
        super(application);
        this.userPersonaRepository = UserPersonaRepository.getInstance(application);
        setupMediatorLiveData();
    }

    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // 只观察用户UserPersona列表
        userPersonasLiveData.addSource(userPersonaRepository.getUserPersonas(), userPersonasLiveData::setValue);
    }
    
    /**
     * 获取用户创建的UserPersona列表LiveData
     * @return 用户UserPersona列表的LiveData对象
     */
    public LiveData<List<UserPersona>> getUserPersonas() {
        return userPersonasLiveData;
    }
    
    /**
     * 删除用户UserPersona
     * @param userPersona 要删除的UserPersona
     * @return 如果成功删除返回true，如果不存在则返回false
     */
    public boolean removeUserPersona(UserPersona userPersona) {
        return userPersonaRepository.removeUserPersona(userPersona);
    }
}