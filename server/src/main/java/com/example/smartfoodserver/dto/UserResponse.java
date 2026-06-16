package com.example.smartfoodserver.dto;

public class UserResponse {
    private boolean success;
    private String message;
    private Long userId;
    private String username;
    private String nickname;

    public UserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public UserResponse(boolean success, String message, Long userId, String username, String nickname) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }
}
