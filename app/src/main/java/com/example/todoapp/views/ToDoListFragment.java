package com.example.todoapp.views;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.adapters.ToDoAdapter;
import com.example.todoapp.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioButton;

public class ToDoListFragment extends Fragment implements ToDoAdapter.OnToDoClickListener {
    private RecyclerView recyclerView;
    private ToDoAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private int selectedPriority = -1;
    private int selectedStatus = -1;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private Handler mainHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new ToDoAdapter(this);
        recyclerView.setAdapter(adapter);

        // Initialize handlers
        backgroundThread = new HandlerThread("TodoProcessingThread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
        mainHandler = new Handler(Looper.getMainLooper());
        
        loadTodos();
        
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        backgroundThread.quitSafely();
    }

    private void loadTodos() {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("todos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;

                if (value != null) {
                    // Process todos in background thread
                    backgroundHandler.post(() -> {
                        ArrayList<ToDo> todoList = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ToDo todo = doc.toObject(ToDo.class);
                            if (todo != null) {
                                todo.setId(doc.getId());
                                
                                boolean addTodo = true;
                                
                                if (selectedPriority != -1 && todo.getPriority() != selectedPriority) {
                                    addTodo = false;
                                }
                                
                                if (selectedStatus != -1) {
                                    boolean isCompleted = selectedStatus == 1;
                                    if (todo.isCompleted() != isCompleted) {
                                        addTodo = false;
                                    }
                                }
                                
                                if (addTodo) {
                                    todoList.add(todo);
                                }
                            }
                        }

                        // Update UI in main thread
                        mainHandler.post(() -> {
                            adapter.setTodos(todoList);
                        });
                    });
                }
            });
    }

    @Override
    public void onToDoClick(ToDo toDo) {
        showTodoDetailsDialog(toDo);
    }

    private void showTodoDetailsDialog(ToDo todo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_todo_details, null);
        
        TextView titleText = view.findViewById(R.id.todoTitle);
        TextView descriptionText = view.findViewById(R.id.todoDescription);
        TextView priorityText = view.findViewById(R.id.todoPriority);
        TextView dueDateText = view.findViewById(R.id.todoDueDate);
        Button editButton = view.findViewById(R.id.editButton);
        Button deleteButton = view.findViewById(R.id.deleteButton);

        titleText.setText(todo.getTitle());
        descriptionText.setText(todo.getDescription());
        priorityText.setText(todo.getPriorityText());
        if (todo.getDueDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dueDateText.setText("Due Date: " + dateFormat.format(todo.getDueDate()));
        }

        AlertDialog dialog = builder.setView(view).create();

        editButton.setOnClickListener(v -> {
            dialog.dismiss();
            showEditTodoBottomSheet(todo);
        });

        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteTodo(todo);
        });

        dialog.show();
    }

    private void showEditTodoBottomSheet(ToDo todo) {
        AddToDoBottomSheet bottomSheet = new AddToDoBottomSheet();
        Bundle args = new Bundle();
        args.putString("todoId", todo.getId());
        args.putString("title", todo.getTitle());
        args.putString("description", todo.getDescription());
        args.putInt("priority", todo.getPriority());
        if (todo.getDueDate() != null) {
            args.putLong("dueDate", todo.getDueDate().getTime());
        }
        bottomSheet.setArguments(args);
        bottomSheet.show(getParentFragmentManager(), "editTodoBottomSheet");
    }

    private void deleteTodo(ToDo todo) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Todo")
            .setMessage("Are you sure you want to delete this todo?")
            .setPositiveButton("Delete", (dialog, which) -> {
                FirebaseFirestore.getInstance()
                    .collection("todos")
                    .document(todo.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> 
                        Toast.makeText(requireContext(), "Todo deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> 
                        Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    public void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        
        RadioGroup priorityGroup = view.findViewById(R.id.priorityGroup);
        RadioGroup statusGroup = view.findViewById(R.id.statusGroup);
        Button applyButton = view.findViewById(R.id.applyFilter);
        
        if (selectedPriority != -1) {
            ((RadioButton) priorityGroup.getChildAt(selectedPriority + 1)).setChecked(true);
        }
        if (selectedStatus != -1) {
            ((RadioButton) statusGroup.getChildAt(selectedStatus + 1)).setChecked(true);
        }
        
        AlertDialog dialog = builder.setView(view).create();
        
        applyButton.setOnClickListener(v -> {
            int priorityId = priorityGroup.getCheckedRadioButtonId();
            int statusId = statusGroup.getCheckedRadioButtonId();
            
            if (priorityId == R.id.allPriority) selectedPriority = -1;
            else if (priorityId == R.id.lowPriority) selectedPriority = 0;
            else if (priorityId == R.id.mediumPriority) selectedPriority = 1;
            else if (priorityId == R.id.highPriority) selectedPriority = 2;
            
            if (statusId == R.id.allStatus) selectedStatus = -1;
            else if (statusId == R.id.completedStatus) selectedStatus = 1;
            else if (statusId == R.id.uncompletedStatus) selectedStatus = 0;
            
            loadTodos();
            dialog.dismiss();
        });
        
        dialog.show();
    }
} 