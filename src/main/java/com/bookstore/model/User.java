package com.bookstore.model;


public class User {
    private String userId;
    private String name;
    private String password;

    public User() {
    }

    public User(String userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;

    }

    public String getPassword() {
        return password;
    }

    public void setUserId(String u) {
        this.userId = u;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setPassword(String p) {
        this.password = p;
    }
}
