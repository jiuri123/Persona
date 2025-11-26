package com.example.demo.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponse {
    @SerializedName("choices")
    private List<ChatChoice> chatChoices;

    public List<ChatChoice> getChoices() { return chatChoices; }
    public void setChoices(List<ChatChoice> chatChoices) { this.chatChoices = chatChoices; }

    public String getFirstMessageContent() {
        if (chatChoices != null && !chatChoices.isEmpty()) {
            ChatChoice firstChatChoice = chatChoices.get(0);
            if (firstChatChoice != null && firstChatChoice.getMessage() != null) {
                return firstChatChoice.getMessage().getContent();
            }
        }
        return null;
    }
}