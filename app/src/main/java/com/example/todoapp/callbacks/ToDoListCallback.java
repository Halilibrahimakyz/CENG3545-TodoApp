package com.example.todoapp.callbacks;

import com.example.todoapp.models.ToDo;
import java.util.List;

public interface ToDoListCallback {
    void onSuccess(List<ToDo> todos);
    void onError(String error);
} 