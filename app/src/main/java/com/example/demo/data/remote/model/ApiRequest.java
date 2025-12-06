package com.example.demo.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 聊天请求数据模型类
 * 表示发送到聊天API的请求体
 * 使用Gson库进行JSON序列化和反序列化
 */
public class ApiRequest {

    // 使用的AI模型名称
    @SerializedName("model")
    private String model;

    // 聊天消息列表
    @SerializedName("messages")
    private List<ApiRequestMessage> messages;

    /**
     * 构造函数
     * @param model AI模型名称
     * @param messages 聊天消息列表
     */
    public ApiRequest(String model, List<ApiRequestMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    // Getter和Setter方法
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<ApiRequestMessage> getMessages() { return messages; }
    public void setMessages(List<ApiRequestMessage> messages) { this.messages = messages; }
}