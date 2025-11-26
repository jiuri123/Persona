package com.example.demo.network;

import com.google.gson.annotations.SerializedName;

public class ChatChoice {
    @SerializedName("message")
    private ChatResponseMessage message;

    public ChatResponseMessage getMessage() { return message; }
    public void setMessage(ChatResponseMessage message) { this.message = message; }
}