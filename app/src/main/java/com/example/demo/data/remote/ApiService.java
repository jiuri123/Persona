package com.example.demo.data.remote;

import com.example.demo.data.remote.model.ChatRequest;
import com.example.demo.data.remote.model.ChatResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * API服务接口
 * 定义与聊天API服务器通信的接口
 * 使用Retrofit框架实现HTTP请求
 */
public interface ApiService {

    /**
     * 获取聊天完成接口
     * 发送聊天请求并获取AI回复
     * @param apiKey API密钥，用于身份验证
     * @param chatRequest 聊天请求对象，包含消息列表和其他参数
     * @return Call对象，用于异步执行请求
     */
    @POST("v1/chat/completions")
    Call<ChatResponse> getChatCompletion(
            @Header("Authorization") String apiKey,
            @Body ChatRequest chatRequest
    );
}