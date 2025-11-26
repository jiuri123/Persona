package com.example.demo.model;

/**
 * 聊天消息数据模型类
 * 表示聊天应用中的一条消息
 */
public class ChatMessage {

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
        this.text = text;
        this.isSentByUser = isSentByUser;
    }

    // Getter和Setter方法
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
}