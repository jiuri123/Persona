package com.example.demo.data.remote;

import com.example.demo.data.remote.model.ApiRequest;
import com.example.demo.data.remote.model.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * API服务接口
 * 定义与API服务器通信的接口
 * 使用Retrofit框架实现HTTP请求
 */
public interface ApiService {

    /**
     * 获取API响应接口
     * 发送API请求并获取响应
     * @param apiKey API密钥，用于身份验证
     * @param apiRequest API请求对象，包含请求参数
     * @return Call对象，用于异步执行请求
     */
    @POST("v1/chat/completions")
    Call<ApiResponse> getApiResponse(
            @Header("Authorization") String apiKey,
            @Body ApiRequest apiRequest
    );
}