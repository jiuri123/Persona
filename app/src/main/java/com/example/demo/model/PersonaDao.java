package com.example.demo.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Persona数据访问对象接口
 * 定义了对Persona表的增删改查操作
 */
@Dao
public interface PersonaDao {

    /**
     * 插入Persona
     * @param persona 要插入的Persona对象
     */
    @Insert
    void insertPersona(Persona persona);

    /**
     * 删除Persona
     * @param persona 要删除的Persona对象
     */
    @Delete
    void deletePersona(Persona persona);

    /**
     * 更新Persona
     * @param persona 要更新的Persona对象
     */
    @Update
    void updatePersona(Persona persona);

    /**
     * 获取所有Persona
     * @return 所有Persona的LiveData列表
     */
    @Query("SELECT * FROM personas")
    LiveData<List<Persona>> getAllPersonas();

    /**
     * 根据名称获取Persona
     * @param name Persona的名称
     * @return 匹配的Persona对象
     */
    @Query("SELECT * FROM personas WHERE name = :name")
    Persona getPersonaByName(String name);
    
    /**
     * 根据ID获取Persona
     * @param id Persona的唯一标识符
     * @return 匹配的Persona对象
     */
    @Query("SELECT * FROM personas WHERE id = :id")
    Persona getPersonaById(long id);
}