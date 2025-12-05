package com.example.demo.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 聊天响应数据模型类
 * 表示从聊天API接收的完整响应
 * 包含一个或多个AI生成的选择项
 */
public class ApiResponse {
    // API返回的选择列表
    @SerializedName("choices")
    private List<ApiResponseMessageChoice> apiResponseMessageChoices;

    // Getter和Setter方法
    public List<ApiResponseMessageChoice> getChoices() { return apiResponseMessageChoices; }
    public void setChoices(List<ApiResponseMessageChoice> apiResponseMessageChoices) { this.apiResponseMessageChoices = apiResponseMessageChoices; }

    /**
     * 获取第一个消息的内容
     * 便捷方法，用于快速获取AI的回复内容
     * @return 第一个选择项中的消息内容，如果没有则返回null
     */
    public String getFirstMessageContent() {
        if (apiResponseMessageChoices != null && !apiResponseMessageChoices.isEmpty()) {
            ApiResponseMessageChoice firstApiResponseMessageChoice = apiResponseMessageChoices.get(0);
            if (firstApiResponseMessageChoice != null && firstApiResponseMessageChoice.getMessage() != null) {
                return firstApiResponseMessageChoice.getMessage().getContent();
            }
        }
        return null;
    }
}