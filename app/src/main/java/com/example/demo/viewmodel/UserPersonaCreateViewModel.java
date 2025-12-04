package com.example.demo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.demo.model.Persona;
import com.example.demo.data.repository.UserPersonaRepository;

import java.util.List;

/**
 * 创建角色ViewModel类
 * 管理角色创建相关的数据和操作
 * 直接使用UserPersonaRepository处理API调用和数据管理
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaCreateViewModel extends AndroidViewModel {

    // 使用MediatorLiveData作为数据中转
    private final MediatorLiveData<Boolean> isLoadingLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<Persona> generatedPersonaLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<String> errorLiveData = new MediatorLiveData<>();
    private final MediatorLiveData<List<Persona>> userPersonasLiveData = new MediatorLiveData<>();
    // 用于 O(1) 快速查找的集合缓存
    private final java.util.Set<String> userPersonaNames = new java.util.HashSet<>();
    
    // Persona数据仓库
    private final UserPersonaRepository userPersonaRepository;

    /**
     * 构造函数
     * 初始化UserPersonaRepository实例
     * @param application Application实例
     */
    public UserPersonaCreateViewModel(Application application) {
        super(application);
        this.userPersonaRepository = UserPersonaRepository.getInstance(application);
        setupMediatorLiveData();
    }
    
    /**
     * 设置MediatorLiveData观察Repository的LiveData
     */
    private void setupMediatorLiveData() {
        // 观察加载状态
        isLoadingLiveData.addSource(userPersonaRepository.getIsLoading(), isLoadingLiveData::setValue);

        // 观察AI生成的临时Persona对象
        generatedPersonaLiveData.addSource(userPersonaRepository.getGeneratedPersona(), generatedPersonaLiveData::setValue);
        
        // 观察用户角色列表
        userPersonasLiveData.addSource(userPersonaRepository.getUserPersonas(), userPersonas -> {
            userPersonaNames.clear();
            if (userPersonas != null) {
                for (Persona persona : userPersonas) {
                    userPersonaNames.add(persona.getName());
                }
            }
            // 将获取到的userPersonas设置到userPersonasLiveData中
            userPersonasLiveData.setValue(userPersonas);
        });

        // 观察错误信息
        errorLiveData.addSource(userPersonaRepository.getError(), errorLiveData::setValue);
    }

    /**
     * 获取生成的角色LiveData
     * @return 角色的LiveData对象
     */
    public LiveData<Persona> getGeneratedPersona() {
        return generatedPersonaLiveData;
    }

    /**
     * 获取加载状态LiveData
     * @return 加载状态的LiveData对象
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    /**
     * 获取错误信息LiveData
     * @return 错误信息的LiveData对象
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    /**
     * 清除生成的Persona对象
     */
    public void clearGeneratedPersona() {
        userPersonaRepository.clearGeneratedPersona();
    }

    /**
     * 生成角色详情
     * 调用UserPersonaRepository生成角色名称和背景故事
     */
    public void generatePersonaDetails() {
        userPersonaRepository.generatePersonaDetails();
    }

    /**
     * 创建并保存Persona
     * @param name 角色名称
     * @param avatarDrawableId 头像资源ID
     * @param avatarUri 头像URI（用于从相册选择的图片）
     * @param signature 个性签名
     * @param backgroundStory 背景故事
     * @param gender 性别
     * @param age 年龄
     * @param personality 性格
     * @param relationship 关系（和我的关系）
     * @return 是否成功创建并保存角色
     */
    public boolean createPersonaAndSave(String name, int avatarDrawableId, String avatarUri, String signature, String backgroundStory,
                                 String gender, int age, String personality, String relationship) {
        Persona newPersona = new Persona(0, name, avatarDrawableId, avatarUri, signature, backgroundStory, 
                                         gender, age, personality, relationship);
        
        // 直接通过UserPersonaRepository将创建的Persona添加到仓库
        return userPersonaRepository.addUserPersona(newPersona);
    }

    // 检查角色名称是否存在
    public boolean isPersonaNameExists(String myPersonaName) {
        return userPersonaNames.contains(myPersonaName.trim());
    }

    // 获取用户角色列表LiveData
    public LiveData<List<Persona>> getUserPersonas() {
        return userPersonasLiveData;
    }
}