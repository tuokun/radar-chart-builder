package com.radarchart.dto;

public class AuthResponse {
    private String token;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token.substring(0, Math.min(20, token.length())) + "...'" +
                ", user=" + user +
                '}';
    }
}
