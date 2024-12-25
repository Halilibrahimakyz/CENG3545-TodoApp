package com.example.todoapp.callbacks;

public interface AuthCallback {
    void onSuccess(String message);
    void onError(String error);
} 