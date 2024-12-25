package com.example.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.controllers.AuthController;
import com.example.todoapp.views.LoginActivity;
import com.example.todoapp.views.AddToDoActivity;
import com.example.todoapp.views.ToDoListFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private AuthController authController;
    private FloatingActionButton addTodoFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = new AuthController();
        initViews();
        setupListeners();

        // Fragment'ı container'a ekle
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ToDoListFragment())
                    .commit();
        }
    }

    private void initViews() {
        addTodoFab = findViewById(R.id.addTodoFab);
    }

    private void setupListeners() {
        addTodoFab.setOnClickListener(v -> 
            startActivity(new Intent(this, AddToDoActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            authController.handleLogout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}