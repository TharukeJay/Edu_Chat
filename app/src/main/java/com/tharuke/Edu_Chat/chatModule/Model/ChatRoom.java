package com.tharuke.Edu_Chat.chatModule.Model;

public class ChatRoom {

    private String id;
    private String displayName;
    private String name;
    private String thumbnail;
    private int maxUsersAllowed;
    private int activeUserCount;


    public ChatRoom(String id, String displayName, String name, String thumbnail, int maxUsersAllowed, int activeUserCount) {
        this.id = id;
        this.displayName = displayName;
        this.name = name;
        this.thumbnail = thumbnail;
        this.maxUsersAllowed = maxUsersAllowed;
        this.activeUserCount = activeUserCount;
    }

    public ChatRoom() {
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getMaxUsersAllowed() {
        return maxUsersAllowed;
    }

    public int getActiveUserCount() {
        return activeUserCount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setMaxUsersAllowed(int maxUsersAllowed) {
        this.maxUsersAllowed = maxUsersAllowed;
    }

    public void setActiveUserCount(int activeUserCount) {
        this.activeUserCount = activeUserCount;
    }
}
