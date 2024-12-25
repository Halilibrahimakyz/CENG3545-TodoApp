package com.example.todoapp.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.todoapp.R;
import com.example.todoapp.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import com.google.firebase.Timestamp;

public class AddToDoBottomSheet extends BottomSheetDialogFragment {
    private EditText titleInput, descriptionInput;
    private Spinner prioritySpinner;
    private Button datePickerButton, saveButton;
    private Date selectedDate;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String todoId;
    private String editTitle;
    private String editDescription;
    private int editPriority;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            todoId = args.getString("todoId");
            editTitle = args.getString("title");
            editDescription = args.getString("description");
            editPriority = args.getInt("priority");
            if (args.containsKey("dueDate")) {
                selectedDate = new Date(args.getLong("dueDate"));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_todo, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews(view);
        setupSpinner();
        setupListeners();

        if (todoId != null) {
            titleInput.setText(editTitle);
            descriptionInput.setText(editDescription);
            prioritySpinner.setSelection(editPriority);
            if (selectedDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                datePickerButton.setText(dateFormat.format(selectedDate));
            }
        }

        return view;
    }

    private void initViews(View view) {
        titleInput = view.findViewById(R.id.todoTitleInput);
        descriptionInput = view.findViewById(R.id.todoDescriptionInput);
        prioritySpinner = view.findViewById(R.id.prioritySpinner);
        datePickerButton = view.findViewById(R.id.datePickerButton);
        saveButton = view.findViewById(R.id.saveTodoButton);
    }

    private void setupSpinner() {
        String[] priorities = {"Düşük", "Orta", "Yüksek"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        datePickerButton.setOnClickListener(v -> showDatePicker());
        saveButton.setOnClickListener(v -> saveTodo());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
            (view, year1, monthOfYear, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year1, monthOfYear, dayOfMonth);
                selectedDate = calendar.getTime();
                datePickerButton.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
            }, year, month, day);
        
        datePickerDialog.show();
    }

    private void saveTodo() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        int priority = prioritySpinner.getSelectedItemPosition();
        
        if (title.isEmpty()) {
            titleInput.setError("Başlık gerekli");
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Lütfen bir tarih seçin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (todoId != null) {
            // Güncelleme
            db.collection("todos")
                .document(todoId)
                .update(
                    "title", title,
                    "description", description,
                    "priority", priority,
                    "dueDate", selectedDate,
                    "updatedAt", new Timestamp(new Date())
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Todo güncellendi", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            // Yeni ekleme
            ToDo todo = new ToDo(title, description, auth.getCurrentUser().getUid(), selectedDate, priority);
            db.collection("todos")
                .add(todo)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(requireContext(), "Todo eklendi", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }
} 