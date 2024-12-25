package com.example.todoapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.controllers.AuthController;
import com.example.todoapp.utils.ToolbarUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private TextView userNameText;
    private TextView userEmailText;
    private Button logoutButton;
    private AuthController authController;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        authController = new AuthController();

        ToolbarUtil.setupToolbarWithBack(this, "Profil");
        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        userNameText = findViewById(R.id.userNameText);
        userEmailText = findViewById(R.id.userEmailText);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();

            if (name != null && !name.isEmpty()) {
                userNameText.setText(name);
            } else {
                userNameText.setText("Kullanıcı");
            }

            if (email != null) {
                userEmailText.setText(email);
            }
        }
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> {
            authController.handleLogout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
} 