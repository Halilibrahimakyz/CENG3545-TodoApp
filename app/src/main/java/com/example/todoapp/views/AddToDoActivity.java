package com.example.todoapp.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todoapp.R;
import com.example.todoapp.callbacks.ToDoCallback;
import com.example.todoapp.controllers.ToDoController;
import java.util.Calendar;
import java.util.Date;

public class AddToDoActivity extends AppCompatActivity {
    private EditText titleInput;
    private EditText descriptionInput;
    private Button dueDateButton;
    private Spinner prioritySpinner;
    private Button saveButton;
    private ToDoController todoController;
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        todoController = new ToDoController();
        initViews();
        setupListeners();
    }

    private void initViews() {
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        dueDateButton = findViewById(R.id.dueDateButton);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupListeners() {
        dueDateButton.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> handleSave());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDate = selectedCalendar.getTime();
                dueDateButton.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void handleSave() {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        int priority = prioritySpinner.getSelectedItemPosition() + 1;

        if (title.isEmpty() || description.isEmpty() || selectedDate == null) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        todoController.createToDo(title, description, selectedDate, priority, new ToDoCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(AddToDoActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddToDoActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
} 