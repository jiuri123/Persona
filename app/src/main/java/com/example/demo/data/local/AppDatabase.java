package com.example.demo.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.demo.model.OtherPersona;
import com.example.demo.model.UserPersona;

/**
 * 应用数据库类
 * 继承自RoomDatabase，使用单例模式创建数据库实例
 */
@Database(entities = {UserPersona.class, OtherPersona.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 数据库名称
    private static final String DATABASE_NAME = "app_database";

    // 单例实例
    private static volatile AppDatabase instance;

    /**
     * 获取单例实例
     * @param context 上下文
     * @return AppDatabase的单例实例
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    DATABASE_NAME
            )
                    .fallbackToDestructiveMigration() // 仅用于开发和测试阶段
                    .build();
        }
        return instance;
    }

    /**
     * 获取UserPersonaDao实例
     * @return UserPersonaDao实例
     */
    public abstract UserPersonaDao userPersonaDao();
    
    /**
     * 获取OtherPersonaDao实例
     * @return OtherPersonaDao实例
     */
    public abstract OtherPersonaDao otherPersonaDao();
}