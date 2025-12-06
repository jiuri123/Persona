package com.example.demo.model;

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

    /**
     * 构造函数
     * @param text 消息文本内容
     * @param isSentByUser 消息是否由用户发送
     */
    public ChatMessage(String text, boolean isSentByUser) {
        this.id = UUID.randomUUID();
        this.text = text;
        this.isSentByUser = isSentByUser;
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
}