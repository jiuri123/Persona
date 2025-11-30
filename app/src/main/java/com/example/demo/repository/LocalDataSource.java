package com.example.demo.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.demo.model.AppDatabase;
import com.example.demo.model.Persona;
import com.example.demo.model.PersonaDao;

import java.util.List;

/**
 * 本地数据源类
 * 封装了对本地数据库的访问，提供了增删改查方法
 */
public class LocalDataSource {

    // Persona数据访问对象
    private final PersonaDao personaDao;

    /**
     * 构造函数
     * @param context 上下文
     */
    public LocalDataSource(Context context) {
        // 获取数据库实例
        AppDatabase database = AppDatabase.getInstance(context);
        // 获取PersonaDao实例
        this.personaDao = database.personaDao();
    }

    /**
     * 插入Persona
     * @param persona 要插入的Persona对象
     */
    public void insertPersona(Persona persona) {
        personaDao.insertPersona(persona);
    }

    /**
     * 删除Persona
     * @param persona 要删除的Persona对象
     */
    public void deletePersona(Persona persona) {
        personaDao.deletePersona(persona);
    }

    /**
     * 更新Persona
     * @param persona 要更新的Persona对象
     */
    public void updatePersona(Persona persona) {
        personaDao.updatePersona(persona);
    }

    /**
     * 获取所有Persona
     * @return 所有Persona的LiveData列表
     */
    public LiveData<List<Persona>> getAllPersonas() {
        return personaDao.getAllPersonas();
    }

    /**
     * 根据名称获取Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象
     */
    public Persona getPersonaByName(String name) {
        return personaDao.getPersonaByName(name);
    }
}