package com.example.todoapp.services;

import com.example.todoapp.callbacks.ToDoCallback;
import com.example.todoapp.callbacks.ToDoListCallback;
import com.example.todoapp.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ToDoService {
    private final FirebaseFirestore db;
    private static final String COLLECTION_NAME = "todos";

    public ToDoService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void createToDo(ToDo todo, ToDoCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        todo.setUserId(userId);

        db.collection(COLLECTION_NAME)
                .add(todo)
                .addOnSuccessListener(documentReference -> {
                    callback.onSuccess("ToDo added successfully!");
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to add ToDo: " + e.getMessage());
                });
    }

    public void getToDos(boolean includeCompleted, ToDoListCallback callback) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("completed", includeCompleted)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ToDo> todos = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ToDo todo = doc.toObject(ToDo.class);
                        if (todo != null) {
                            todo.setId(doc.getId());
                            todos.add(todo);
                        }
                    }
                    callback.onSuccess(todos);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to fetch ToDos: " + e.getMessage());
                });
    }

    public void updateToDo(ToDo todo, ToDoCallback callback) {
        if (todo.getId() == null) {
            callback.onError("ToDo ID not found!");
            return;
        }

        db.collection(COLLECTION_NAME)
                .document(todo.getId())
                .set(todo)
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("ToDo updated successfully!");
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to update ToDo: " + e.getMessage());
                });
    }

    public void deleteToDo(String todoId, ToDoCallback callback) {
        db.collection(COLLECTION_NAME)
                .document(todoId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess("ToDo deleted successfully!");
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to delete ToDo: " + e.getMessage());
                });
    }
} 