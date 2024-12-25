package com.example.todoapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class ToDo {
    private String id;
    private String title;
    private String description;
    
    @PropertyName("completed")
    private boolean isCompleted;
    
    private int priority;
    private Date dueDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String userId;

    public ToDo() {
        // Firestore için boş constructor gerekli
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
        this.isCompleted = false;
    }

    public ToDo(String title, String description, String userId, Date dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.isCompleted = false;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getter ve Setter metodları
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title;
        this.updatedAt = Timestamp.now();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
        this.updatedAt = Timestamp.now();
    }
    
    @PropertyName("completed")
    public boolean isCompleted() { return isCompleted; }
    
    @PropertyName("completed")
    public void setCompleted(boolean completed) { 
        this.isCompleted = completed;
        this.updatedAt = Timestamp.now();
    }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { 
        this.priority = priority;
        this.updatedAt = Timestamp.now();
    }
    
    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { 
        this.dueDate = dueDate;
        this.updatedAt = Timestamp.now();
    }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // Öncelik seviyelerini string olarak almak için yardımcı metod
    public String getPriorityText() {
        switch(priority) {
            case 0: return "Düşük";
            case 1: return "Orta";
            case 2: return "Yüksek";
            default: return "Belirsiz";
        }
    }
} 