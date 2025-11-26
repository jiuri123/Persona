package com.example.demo.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("v1/chat/completions")
    Call<ChatResponse> getChatCompletion(
            @Header("Authorization") String apiKey,
            @Body ChatRequest chatRequest
    );
}