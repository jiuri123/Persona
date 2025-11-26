package com.example.demo.network;

import com.google.gson.annotations.SerializedName;

public class ChatResponseMessage {
    @SerializedName("role")
    private String role;

    @SerializedName("content")
    private String content;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}