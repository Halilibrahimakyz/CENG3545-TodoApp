package com.example.todoapp.callbacks;

public interface ToDoCallback {
    void onSuccess(String message);
    void onError(String error);
} 