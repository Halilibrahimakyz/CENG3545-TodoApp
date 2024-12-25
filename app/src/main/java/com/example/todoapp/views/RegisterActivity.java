package com.example.todoapp.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.callbacks.AuthCallback;
import com.example.todoapp.controllers.AuthController;

public class RegisterActivity extends AppCompatActivity {
    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button registerButton;
    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = new AuthController();
        initViews();
        setupListeners();
    }

    private void initViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerButton = findViewById(R.id.registerButton);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        authController.handleRegister(name, email, password, new AuthCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "Registration failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
} 