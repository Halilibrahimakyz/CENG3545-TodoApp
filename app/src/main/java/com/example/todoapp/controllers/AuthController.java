package com.example.todoapp.controllers;

import com.example.todoapp.callbacks.AuthCallback;
import com.example.todoapp.models.User;
import com.example.todoapp.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthController {
    private final AuthService authService;
    private FirebaseAuth mAuth;

    public AuthController() {
        this.authService = new AuthService();
        mAuth = FirebaseAuth.getInstance();
    }

    public void handleLogin(String email, String password, AuthCallback callback) {
        authService.login(email, password)
                .addOnSuccessListener(authResult -> {
                    callback.onSuccess("Giriş başarılı!");
                })
                .addOnFailureListener(e -> {
                    callback.onError("Giriş başarısız: " + e.getMessage());
                });
    }

    public void handleRegister(String email, String password, String name, AuthCallback callback) {
        authService.register(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User newUser = new User(uid, email, name, password);
                    saveUserToFirestore(newUser, callback);
                })
                .addOnFailureListener(e -> {
                    callback.onError("Kayıt başarısız: " + e.getMessage());
                });
    }

    private void saveUserToFirestore(User user, AuthCallback callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess("Kayıt başarılı!"))
                .addOnFailureListener(e -> callback.onError("Kullanıcı bilgileri kaydedilemedi: " + e.getMessage()));
    }

    public void handleLogout() {
        mAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
} 