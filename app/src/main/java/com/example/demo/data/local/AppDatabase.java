package com.example.demo.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.demo.model.Persona;

/**
 * 应用数据库类
 * 继承自RoomDatabase，使用单例模式创建数据库实例
 */
@Database(entities = {Persona.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // 数据库名称
    private static final String DATABASE_NAME = "demo_database";

    // 单例实例
    private static volatile AppDatabase instance;
    
    // 迁移策略：从版本1到版本2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 添加id列，设置为自增主键
            database.execSQL("ALTER TABLE personas ADD COLUMN id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL DEFAULT 0");
        }
    };

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
                    .addMigrations(MIGRATION_1_2)
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