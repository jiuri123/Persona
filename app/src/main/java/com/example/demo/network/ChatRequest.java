package com.example.demo.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatRequest {

    @SerializedName("model")
    private String model;

    @SerializedName("messages")
    private List<ChatApiMessage> messages;

    @SerializedName("stream")
    private boolean stream = false;

    public ChatRequest(String model, List<ChatApiMessage> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public List<ChatApiMessage> getMessages() { return messages; }
    public void setMessages(List<ChatApiMessage> messages) { this.messages = messages; }
}