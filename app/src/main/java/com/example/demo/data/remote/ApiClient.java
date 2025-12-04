package com.example.demo.data.remote;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API客户端类
 * 提供Retrofit实例和API服务接口的单例实现
 * 用于与聊天API服务器进行通信
 */
public class ApiClient {

    // API基础URL
    public static final String BASE_URL = "https://api.moonshot.cn/";

    // Retrofit实例，单例模式
    private static Retrofit retrofit = null;
    // API服务接口实例，单例模式
    private static ApiService apiService = null;

    /**
     * 获取API服务接口实例
     * 使用单例模式确保只有一个实例
     * @return ApiService实例
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }

    /**
     * 获取Retrofit实例
     * 配置HTTP客户端和转换器
     * @return Retrofit实例
     */
    private static Retrofit getRetrofit() {
        if (retrofit == null) {
            // 创建HTTP日志拦截器，用于调试
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 配置OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // 添加日志拦截器
                    .build();

            // 创建Retrofit实例
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 设置基础URL
                    .client(client) // 设置自定义HTTP客户端
                    .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器，用于JSON解析
                    .build();
        }
        return retrofit;
    }
}