package com.rex.easymusic.EventBus;

public class MessageEvent {
    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    private int  message;

    public MessageEvent(int message) {
        this.message = message;
    }
}
