package com.radarchart.dto.result;

public class AuthResult {
    private String token;
    private UserResult user;

    public AuthResult() {
    }

    public AuthResult(String token, UserResult user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResult getUser() {
        return user;
    }

    public void setUser(UserResult user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResult{" +
                "token='" + token.substring(0, Math.min(20, token.length())) + "...'" +
                ", user=" + user +
                '}';
    }
}
