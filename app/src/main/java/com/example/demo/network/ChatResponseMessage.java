package com.example.demo.network;

import com.google.gson.annotations.SerializedName;

/**
 * 聊天响应消息数据模型类
 * 表示聊天API返回的消息对象
 * 包含消息角色和内容信息
 */
public class ChatResponseMessage {
    // 消息角色：通常是"assistant"
    @SerializedName("role")
    private String role;

    // 消息内容
    @SerializedName("content")
    private String content;

    // Getter和Setter方法
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}