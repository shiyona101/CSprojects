package com.example.hackathon;

public class ChatMsgs {
    private String content;
    private boolean isSentByUser;

    public ChatMsgs (String content, boolean isSentByUser) {
        this.content = content;
        this.isSentByUser = isSentByUser;
    }

    public String getContent() {
        return content;
    }

    public boolean isSentByUser() {
        return isSentByUser;
    }
}
