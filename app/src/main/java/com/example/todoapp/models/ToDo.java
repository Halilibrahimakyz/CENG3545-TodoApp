package com.example.todoapp.models;

import java.util.Date;

public class ToDo {
    private String id;
    private String userId;
    private String title;
    private String description;
    private Date dueDate;
    private int priority;
    private boolean completed;
    private Date createdAt;

    public ToDo() {}

    public ToDo(String userId, String title, String description, Date dueDate, int priority) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
        this.createdAt = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getDueDate() { return dueDate; }
    public int getPriority() { return priority; }
    public boolean isCompleted() { return completed; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
} 