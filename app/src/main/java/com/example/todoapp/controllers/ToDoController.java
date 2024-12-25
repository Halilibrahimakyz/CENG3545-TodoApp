package com.example.todoapp.controllers;

import com.example.todoapp.callbacks.ToDoCallback;
import com.example.todoapp.callbacks.ToDoListCallback;
import com.example.todoapp.models.ToDo;
import com.example.todoapp.services.ToDoService;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Date;

public class ToDoController {
    private final ToDoService todoService;

    public ToDoController() {
        this.todoService = new ToDoService();
    }

    public void createToDo(String title, String description, Date dueDate, int priority, ToDoCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        ToDo todo = new ToDo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        todo.setPriority(priority);
        todo.setCompleted(false);

        todoService.createToDo(todo, callback);
    }

    public void getToDos(boolean includeCompleted, ToDoListCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        todoService.getToDos(includeCompleted, callback);
    }

    public void updateToDo(ToDo todo, ToDoCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        todoService.updateToDo(todo, callback);
    }

    public void deleteToDo(String todoId, ToDoCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        todoService.deleteToDo(todoId, callback);
    }

    public void toggleToDoStatus(ToDo todo, ToDoCallback callback) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        
        todo.setCompleted(!todo.isCompleted());
        todoService.updateToDo(todo, callback);
    }
} 