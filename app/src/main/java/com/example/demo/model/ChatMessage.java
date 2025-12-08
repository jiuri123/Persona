package com.example.demo.model;

import com.example.demo.data.model.ChatHistory;

import java.util.UUID;

/**
 * 聊天消息数据模型类
 * 表示聊天应用中的一条消息
 */
public class ChatMessage {

    // 消息唯一标识符
    private UUID id;
    // 消息文本内容
    private String text;
    // 消息是否由用户发送（true为用户发送，false为接收）
    private boolean isSentByUser;
    // 头像资源ID
    private int avatarDrawableId;
    // 头像URI
    private String avatarUri;
    // 打字机效果是否已完成
    private boolean isTypewriterComplete;

    /**
     * 构造函数
     * @param text 消息文本内容
     * @param isSentByUser 消息是否由用户发送
     */
    public ChatMessage(String text, boolean isSentByUser) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.avatarDrawableId = 0;
        this.avatarUri = null;
        this.isTypewriterComplete = false;
    }

    /**
     * 构造函数
     * @param text 消息文本内容
     * @param isSentByUser 消息是否由用户发送
     * @param avatarDrawableId 头像资源ID
     * @param avatarUri 头像URI
     */
    public ChatMessage(String text, boolean isSentByUser, int avatarDrawableId, String avatarUri) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.avatarDrawableId = avatarDrawableId;
        this.avatarUri = avatarUri;
        this.isTypewriterComplete = false;
    }

    // Getter和Setter方法
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
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

    public boolean isTypewriterComplete() {
        return isTypewriterComplete;
    }

    public void setTypewriterComplete(boolean typewriterComplete) {
        isTypewriterComplete = typewriterComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ChatMessage that = (ChatMessage) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * 将ChatMessage转换为ChatHistory
     * @param personaType Persona类型（"user"或"other"）
     * @param personaId 关联的Persona ID
     * @return ChatHistory对象
     */
    public ChatHistory toChatHistory(String personaType, long personaId) {
        return new ChatHistory(
                personaId,
                personaType,
                this.id.toString(),
                this.text,
                this.isSentByUser,
                this.avatarDrawableId,
                this.avatarUri,
                System.currentTimeMillis(), // 使用当前时间戳
                this.isTypewriterComplete
        );
    }
    
    /**
     * 将ChatHistory转换为ChatMessage
     * @param chatHistory ChatHistory对象
     * @return ChatMessage对象
     */
    public static ChatMessage fromChatHistory(ChatHistory chatHistory) {
        ChatMessage chatMessage = new ChatMessage(
                chatHistory.getText(),
                chatHistory.isSentByUser(),
                chatHistory.getAvatarDrawableId(),
                chatHistory.getAvatarUri()
        );
        // 设置消息ID，保持与数据库中的一致
        chatMessage.setId(UUID.fromString(chatHistory.getMessageId()));
        // 设置打字机完成状态，保持与数据库中的一致
        chatMessage.setTypewriterComplete(chatHistory.isTypewriterComplete());
        return chatMessage;
    }
}