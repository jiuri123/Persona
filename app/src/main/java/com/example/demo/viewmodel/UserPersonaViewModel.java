package com.example.demo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.demo.model.Persona;

/**
 * 用户角色ViewModel类
 * 负责管理当前用户的Persona
 * 使用LiveData观察数据变化，通知UI更新
 */
public class UserPersonaViewModel extends ViewModel {

    // LiveData对象，用于观察用户角色变化
    private final MutableLiveData<Persona> userPersonaLiveData = new MutableLiveData<>(null);

    /**
     * 获取用户角色LiveData
     * @return 用户角色的LiveData对象
     */
    public LiveData<Persona> getUserPersona() {
        return userPersonaLiveData;
    }

    /**
     * 设置用户角色
     * @param persona 用户角色对象
     */
    public void setUserPersona(Persona persona) {
        userPersonaLiveData.setValue(persona);
    }

    /**
     * 获取当前用户角色
     * @return 当前用户角色，可能为null
     */
    public Persona getCurrentUserPersona() {
        return userPersonaLiveData.getValue();
    }
}