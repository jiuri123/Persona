package com.example.demo.data.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.demo.model.Persona;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地数据源类
 * 封装了对本地数据库的访问，提供了增删改查方法
 * 使用单例模式确保全局只有一个实例
 */
public class LocalDataSource {

    // 单例实例
    private static volatile LocalDataSource instance;
    
    // 线程池，用于执行后台数据库操作
    private final ExecutorService executorService;
    
    // Persona数据访问对象
    private final PersonaDao personaDao;

    /**
     * 私有构造函数，防止外部实例化
     * @param context 上下文
     */
    private LocalDataSource(Context context) {
        // 获取数据库实例
        AppDatabase database = AppDatabase.getInstance(context);
        // 获取PersonaDao实例
        this.personaDao = database.personaDao();
        // 创建单线程线程池，确保数据库操作顺序执行
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    /**
     * 获取单例实例
     * @param context 上下文
     * @return LocalDataSource的单例实例
     */
    public static synchronized LocalDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new LocalDataSource(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 插入Persona
     * @param persona 要插入的Persona对象
     */
    public void insertPersona(Persona persona) {
        executorService.execute(() -> personaDao.insertPersona(persona));
    }

    /**
     * 删除Persona
     * @param persona 要删除的Persona对象
     */
    public void deletePersona(Persona persona) {
        executorService.execute(() -> personaDao.deletePersona(persona));
    }

    /**
     * 获取所有Persona
     * @return 所有Persona的LiveData列表
     */
    public LiveData<List<Persona>> getAllPersonas() {
        return personaDao.getAllPersonas();
    }
}