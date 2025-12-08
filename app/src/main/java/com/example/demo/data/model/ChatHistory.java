package com.example.demo.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 聊天历史记录数据模型类
 * 表示聊天应用中的一条历史消息，用于本地数据库存储
 */
@Entity(tableName = "chat_history")
public class ChatHistory {

    // 主键，自增
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    // 关联的Persona ID
    @ColumnInfo(name = "persona_id")
    private long personaId;
    
    // Persona类型（"user"或"other"）
    @NonNull
    @ColumnInfo(name = "persona_type")
    private String personaType;
    
    // 消息唯一标识符（UUID字符串）
    @NonNull
    @ColumnInfo(name = "message_id")
    private String messageId;
    
    // 消息文本内容
    @NonNull
    @ColumnInfo(name = "text")
    private String text;
    
    // 消息是否由用户发送（true为用户发送，false为接收）
    @ColumnInfo(name = "is_sent_by_user")
    private boolean isSentByUser;
    
    // 头像资源ID
    @ColumnInfo(name = "avatar_drawable_id")
    private int avatarDrawableId;
    
    // 头像URI
    @ColumnInfo(name = "avatar_uri")
    private String avatarUri;
    
    // 消息时间戳
    @NonNull
    @ColumnInfo(name = "timestamp")
    private long timestamp;
    
    // 打字机效果是否已完成
    @ColumnInfo(name = "is_typewriter_complete")
    private boolean isTypewriterComplete;

    /**
     * 构造函数
     * @param personaId 关联的Persona ID
     * @param personaType Persona类型（"user"或"other"）
     * @param messageId 消息唯一标识符（UUID字符串）
     * @param text 消息文本内容
     * @param isSentByUser 消息是否由用户发送
     * @param avatarDrawableId 头像资源ID
     * @param avatarUri 头像URI
     * @param timestamp 消息时间戳
     * @param isTypewriterComplete 打字机效果是否已完成
     */
    public ChatHistory(long personaId, @NonNull String personaType, @NonNull String messageId, @NonNull String text, 
                      boolean isSentByUser, int avatarDrawableId, String avatarUri, @NonNull long timestamp, 
                      boolean isTypewriterComplete) {
        this.personaId = personaId;
        this.personaType = personaType;
        this.messageId = messageId;
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.avatarDrawableId = avatarDrawableId;
        this.avatarUri = avatarUri;
        this.timestamp = timestamp;
        this.isTypewriterComplete = isTypewriterComplete;
    }

    // Getter和Setter方法
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(long personaId) {
        this.personaId = personaId;
    }

    @NonNull
    public String getPersonaType() {
        return personaType;
    }

    public void setPersonaType(@NonNull String personaType) {
        this.personaType = personaType;
    }

    @NonNull
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(@NonNull String messageId) {
        this.messageId = messageId;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }

    public void setSentByUser(boolean sentByUser) {
        isSentByUser = sentByUser;
    }

    public int getAvatarDrawableId() {
        return avatarDrawableId;
    }

    public void setAvatarDrawableId(int avatarDrawableId) {
        this.avatarDrawableId = avatarDrawableId;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    @NonNull
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(@NonNull long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTypewriterComplete() {
        return isTypewriterComplete;
    }

    public void setTypewriterComplete(boolean typewriterComplete) {
        isTypewriterComplete = typewriterComplete;
    }
}
