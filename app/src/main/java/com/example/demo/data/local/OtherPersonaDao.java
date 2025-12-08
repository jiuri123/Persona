package com.example.demo.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.demo.model.OtherPersona;

import java.util.List;

/**
 * OtherPersona数据访问对象接口
 * 定义了对OtherPersona表的增删改查操作
 */
@Dao
public interface OtherPersonaDao {

    /**
     * 插入OtherPersona
     * @param otherPersona 要插入的OtherPersona对象
     */
    @Insert
    void insertOtherPersona(OtherPersona otherPersona);

    /**
     * 删除OtherPersona
     * @param otherPersona 要删除的OtherPersona对象
     */
    @Delete
    void deleteOtherPersona(OtherPersona otherPersona);

    /**
     * 获取所有OtherPersona
     * @return 所有OtherPersona的LiveData列表
     */
    @Query("SELECT * FROM other_personas")
    LiveData<List<OtherPersona>> getAllOtherPersonas();
    
    /**
     * 获取所有OtherPersona，按创建时间降序排序
     * @return 所有OtherPersona的LiveData列表，按创建时间降序排序
     */
    @Query("SELECT * FROM other_personas ORDER BY createdAt DESC")
    LiveData<List<OtherPersona>> getAllOtherPersonasOrderByCreatedAtDesc();
}
