package com.example.todoapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.controllers.AuthController;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private AuthController authController;
    private ImageButton addButton;
    private ImageButton profileButton;
    private ImageButton filterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = new AuthController();
        initViews();
        setupListeners();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ToDoListFragment())
                    .commit();
        }
    }

    private void initViews() {
        addButton = findViewById(R.id.addButton);
        profileButton = findViewById(R.id.profileButton);
        filterButton = findViewById(R.id.filterButton);
    }

    private void setupListeners() {
        addButton.setOnClickListener(v -> {
            AddToDoBottomSheet bottomSheet = new AddToDoBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "addTodoBottomSheet");
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        filterButton.setOnClickListener(v -> {
            ToDoListFragment fragment = (ToDoListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);
            if (fragment != null) {
                fragment.showFilterDialog();
            }
        });
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