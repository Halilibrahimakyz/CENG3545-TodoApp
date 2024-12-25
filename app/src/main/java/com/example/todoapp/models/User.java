package com.example.todoapp.models;

public class User {
    private String uid;
    private String email;
    private String name;
    private String password;

    public User() {}

    public User(String uid, String email, String name, String password) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.password = password;
    }

    // Getters
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPassword() { return password; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
} 