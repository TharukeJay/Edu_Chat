package com.tharuke.Edu_Chat.chatModule.Model;

public class ChatRoomList {

    private String id;
    private String sender;
    private String message;

    public ChatRoomList(String id, String sender, String message) {
        this.id = id;
        this.sender = sender;
        this.message = message;
    }

    public ChatRoomList() {
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
