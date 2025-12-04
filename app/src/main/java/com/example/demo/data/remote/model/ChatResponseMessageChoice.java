package com.example.demo.data.remote.model;

import com.google.gson.annotations.SerializedName;

/**
 * 聊天选择数据模型类
 * 表示聊天API响应中的一个选择项
 * 通常包含AI生成的消息内容
 */
public class ChatResponseMessageChoice {
    // AI生成的消息
    @SerializedName("message")
    private ChatResponseMessage message;

    // Getter和Setter方法
    public ChatResponseMessage getMessage() { return message; }
    public void setMessage(ChatResponseMessage message) { this.message = message; }
}