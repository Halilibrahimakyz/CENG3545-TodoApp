package com.example.todoapp.controllers;

import com.example.todoapp.callbacks.ToDoCallback;
import com.example.todoapp.callbacks.ToDoListCallback;
import com.example.todoapp.models.ToDo;
import com.example.todoapp.services.ToDoService;
import java.util.Date;

public class ToDoController {
    private final ToDoService todoService;
    private final AuthController authController;

    public ToDoController() {
        this.todoService = new ToDoService();
        this.authController = new AuthController();
    }

    public void createToDo(String title, String description, Date dueDate, String priority, ToDoCallback callback) {
        if (authController.getCurrentUser() == null) return;

        ToDo todo = new ToDo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        todo.setPriority(convertPriorityToInt(priority));
        todo.setCompleted(false);

        todoService.createToDo(todo, callback);
    }

    private int convertPriorityToInt(String priority) {
        switch (priority.toLowerCase()) {
            case "high":
                return 3;
            case "medium":
                return 2;
            case "low":
                return 1;
            default:
                return 1;
        }
    }

    public void getToDos(boolean includeCompleted, ToDoListCallback callback) {
        if (authController.getCurrentUser() == null) return;
        
        todoService.getToDos(includeCompleted, callback);
    }

    public void updateToDo(ToDo todo, ToDoCallback callback) {
        if (authController.getCurrentUser() == null) return;
        
        todoService.updateToDo(todo, callback);
    }

    public void deleteToDo(String todoId, ToDoCallback callback) {
        if (authController.getCurrentUser() == null) return;
        
        todoService.deleteToDo(todoId, callback);
    }

    public void toggleToDoStatus(ToDo todo, ToDoCallback callback) {
        if (authController.getCurrentUser() == null) return;
        
        todo.setCompleted(!todo.isCompleted());
        todoService.updateToDo(todo, callback);
    }
} 