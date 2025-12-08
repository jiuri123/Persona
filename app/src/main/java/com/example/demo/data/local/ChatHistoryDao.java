package com.example.demo.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.demo.data.model.ChatHistory;

import java.util.List;

/**
 * 聊天历史记录数据访问对象
 * 提供数据库操作方法，用于操作chat_history表
 */
@Dao
public interface ChatHistoryDao {
    
    /**
     * 插入单条聊天记录
     * @param chatHistory 聊天历史记录对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatHistory chatHistory);
    
    /**
     * 插入多条聊天记录
     * @param chatHistories 聊天历史记录列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChatHistory> chatHistories);
    
    /**
     * 根据Persona类型和ID查询聊天记录（返回LiveData）
     * @param personaType Persona类型（"user"或"other"）
     * @param personaId 关联的Persona ID
     * @return 聊天历史记录列表的LiveData
     */
    @Query("SELECT * FROM chat_history WHERE persona_type = :personaType AND persona_id = :personaId ORDER BY timestamp ASC")
    LiveData<List<ChatHistory>> getChatHistoryByPersona(String personaType, long personaId);
    
    /**
     * 根据Persona类型和ID查询聊天记录（同步方法）
     * @param personaType Persona类型（"user"或"other"）
     * @param personaId 关联的Persona ID
     * @return 聊天历史记录列表
     */
    @Query("SELECT * FROM chat_history WHERE persona_type = :personaType AND persona_id = :personaId ORDER BY timestamp ASC")
    List<ChatHistory> getChatHistoryByPersonaSync(String personaType, long personaId);
    
    /**
     * 删除指定Persona的聊天记录
     * @param personaType Persona类型（"user"或"other"）
     * @param personaId 关联的Persona ID
     */
    @Query("DELETE FROM chat_history WHERE persona_type = :personaType AND persona_id = :personaId")
    void deleteChatHistoryByPersona(String personaType, long personaId);
    
    /**
     * 删除所有聊天记录
     */
    @Query("DELETE FROM chat_history")
    void deleteAll();
    
    /**
     * 更新消息的打字机完成状态
     * @param messageId 消息ID
     * @param isTypewriterComplete 打字机效果是否已完成
     */
    @Query("UPDATE chat_history SET is_typewriter_complete = :isTypewriterComplete WHERE message_id = :messageId")
    void updateTypewriterStatus(String messageId, boolean isTypewriterComplete);
}
