package com.example.todoapp;

import com.example.todoapp.models.ToDo;
import com.google.firebase.Timestamp;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

public class ToDoTest {
    
   @Test
public void testBasicTodo() {
    String title = "Go to the store";
    String description = "Buy milk and bread";
    String userId = "user123";
    Date dueDate = new Date();
    int priority = 1;

    ToDo todo = new ToDo(title, description, userId, dueDate, priority);

    assertEquals(title, todo.getTitle());
    assertEquals(description, todo.getDescription());
    assertEquals(userId, todo.getUserId());
    assertEquals(dueDate, todo.getDueDate());
    assertEquals(priority, todo.getPriority());
    assertFalse(todo.isCompleted());
}
} 