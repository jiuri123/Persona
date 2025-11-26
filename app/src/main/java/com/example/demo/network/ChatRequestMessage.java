package com.example.demo.network;

import com.google.gson.annotations.SerializedName;

/**
 * 聊天API消息数据模型类
 * 表示发送到聊天API或从API接收的消息
 * 包含角色（用户/助手）和消息内容
 */
public class ChatRequestMessage {

    // 消息角色：user（用户）、assistant（助手）或system（系统）
    @SerializedName("role")
    private String role;

    // 消息内容
    @SerializedName("content")
    private String content;

    /**
     * 构造函数
     * @param role 消息角色
     * @param content 消息内容
     */
    public ChatRequestMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    // Getter和Setter方法
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}