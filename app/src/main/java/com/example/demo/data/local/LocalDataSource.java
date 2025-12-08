package com.example.demo.data.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.demo.model.OtherPersona;
import com.example.demo.model.UserPersona;

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

    // UserPersona数据访问对象
    private final UserPersonaDao userPersonaDao;

    // OtherPersona数据访问对象
    private final OtherPersonaDao otherPersonaDao;

    /**
     * 私有构造函数，防止外部实例化
     * @param context 上下文
     */
    private LocalDataSource(Context context) {
        // 获取数据库实例
        AppDatabase database = AppDatabase.getInstance(context);
        // 获取PersonaDao实例
        this.userPersonaDao = database.userPersonaDao();
        // 获取OtherPersonaDao实例
        this.otherPersonaDao = database.otherPersonaDao();
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
     * 插入UserPersona
     * @param userPersona 要插入的UserPersona对象
     */
    public void insertUserPersona(UserPersona userPersona) {
        // 设置当前时间戳
        userPersona.setCreatedAt(System.currentTimeMillis());
        executorService.execute(() -> userPersonaDao.insertUserPersona(userPersona));
    }

    /**
     * 删除UserPersona
     * @param userPersona 要删除的UserPersona对象
     */
    public void deleteUserPersona(UserPersona userPersona) {
        executorService.execute(() -> userPersonaDao.deleteUserPersona(userPersona));
    }

    /**
     * 获取所有UserPersona
     * @return 所有UserPersona的LiveData列表
     */
    public LiveData<List<UserPersona>> getAllUserPersonas() {
        return userPersonaDao.getAllUserPersonas();
    }
    
    /**
     * 获取所有UserPersona，按创建时间降序排序
     * @return 所有UserPersona的LiveData列表，按创建时间降序排序
     */
    public LiveData<List<UserPersona>> getAllUserPersonasOrderByCreatedAtDesc() {
        return userPersonaDao.getAllUserPersonasOrderByCreatedAtDesc();
    }
    
    /**
     * 插入OtherPersona
     * @param otherPersona 要插入的OtherPersona对象
     */
    public void insertOtherPersona(OtherPersona otherPersona) {
        // 设置当前时间戳
        otherPersona.setCreatedAt(System.currentTimeMillis());
        executorService.execute(() -> otherPersonaDao.insertOtherPersona(otherPersona));
    }
    
    /**
     * 删除OtherPersona
     * @param otherPersona 要删除的OtherPersona对象
     */
    public void deleteOtherPersona(OtherPersona otherPersona) {
        executorService.execute(() -> otherPersonaDao.deleteOtherPersona(otherPersona));
    }
    
    /**
     * 获取所有OtherPersona
     * @return 所有OtherPersona的LiveData列表
     */
    public LiveData<List<OtherPersona>> getAllOtherPersonas() {
        return otherPersonaDao.getAllOtherPersonas();
    }
    
    /**
     * 获取所有OtherPersona，按创建时间降序排序
     * @return 所有OtherPersona的LiveData列表，按创建时间降序排序
     */
    public LiveData<List<OtherPersona>> getAllOtherPersonasOrderByCreatedAtDesc() {
        return otherPersonaDao.getAllOtherPersonasOrderByCreatedAtDesc();
    }
}