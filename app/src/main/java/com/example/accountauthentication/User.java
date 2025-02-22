package com.example.accountauthentication;

public class User {
    private String email;
    private String name;
    private String avatarUrl;

    public User(String email, String name, String avatarUrl) {
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }

    // Getters
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
}
