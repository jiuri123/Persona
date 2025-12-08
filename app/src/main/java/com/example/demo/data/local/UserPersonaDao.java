package com.example.demo.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.demo.model.UserPersona;

import java.util.List;

/**
 * UserPersona数据访问对象接口
 * 定义了对UserPersona表的增删改查操作
 */
@Dao
public interface UserPersonaDao {

    /**
     * 插入UserPersona
     * @param userPersona 要插入的UserPersona对象
     */
    @Insert
    void insertUserPersona(UserPersona userPersona);

    /**
     * 删除UserPersona
     * @param userPersona 要删除的UserPersona对象
     */
    @Delete
    void deleteUserPersona(UserPersona userPersona);

    /**
     * 获取所有UserPersona
     * @return 所有UserPersona的LiveData列表
     */
    @Query("SELECT * FROM user_personas")
    LiveData<List<UserPersona>> getAllUserPersonas();
}