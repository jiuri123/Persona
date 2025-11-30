package com.example.demo.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * 应用数据库类
 * 继承自RoomDatabase，使用单例模式创建数据库实例
 */
@Database(entities = {Persona.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 数据库名称
    private static final String DATABASE_NAME = "demo_database";

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
                    .allowMainThreadQueries() // 允许在主线程执行查询（仅用于开发，生产环境应避免）
                    .build();
        }
        return instance;
    }

    /**
     * 获取PersonaDao实例
     * @return PersonaDao实例
     */
    public abstract PersonaDao personaDao();
}