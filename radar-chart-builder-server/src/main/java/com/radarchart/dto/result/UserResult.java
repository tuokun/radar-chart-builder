package com.radarchart.dto.result;

import java.time.LocalDateTime;

public class UserResult {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static UserResult fromEntity(com.radarchart.entity.User user) {
        UserResult result = new UserResult();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        result.setNickname(user.getNickname());
        result.setCreatedAt(user.getCreatedAt());
        return result;
    }

    @Override
    public String toString() {
        return "UserResult{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
