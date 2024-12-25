package com.example.todoapp.views;

import android.os.Bundle;
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
    private int selectedPriority = -1; // -1 tümü
    private int selectedStatus = -1; // -1 tümü

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        adapter = new ToDoAdapter(this);
        recyclerView.setAdapter(adapter);
        
        loadTodos();
        
        return view;
    }

    private void loadTodos() {
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("todos")
            .whereEqualTo("userId", userId)
            .addSnapshotListener((value, error) -> {
                if (error != null) return;

                ArrayList<ToDo> todoList = new ArrayList<>();
                if (value != null) {
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        ToDo todo = doc.toObject(ToDo.class);
                        if (todo != null) {
                            todo.setId(doc.getId());
                            
                            // Filtreleme
                            boolean addTodo = true;
                            
                            // Öncelik filtresi
                            if (selectedPriority != -1 && todo.getPriority() != selectedPriority) {
                                addTodo = false;
                            }
                            
                            // Durum filtresi
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
                }
                adapter.setTodos(todoList);
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
            dueDateText.setText("Son Tarih: " + dateFormat.format(todo.getDueDate()));
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
            .setTitle("Todo'yu Sil")
            .setMessage("Bu todo'yu silmek istediğinizden emin misiniz?")
            .setPositiveButton("Sil", (dialog, which) -> {
                FirebaseFirestore.getInstance()
                    .collection("todos")
                    .document(todo.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> 
                        Toast.makeText(requireContext(), "Todo silindi", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> 
                        Toast.makeText(requireContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            })
            .setNegativeButton("İptal", null)
            .show();
    }

    public void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_filter, null);
        
        RadioGroup priorityGroup = view.findViewById(R.id.priorityGroup);
        RadioGroup statusGroup = view.findViewById(R.id.statusGroup);
        Button applyButton = view.findViewById(R.id.applyFilter);
        
        // Önceki seçimleri ayarla
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
            
            // Öncelik filtresini ayarla
            if (priorityId == R.id.allPriority) selectedPriority = -1;
            else if (priorityId == R.id.lowPriority) selectedPriority = 0;
            else if (priorityId == R.id.mediumPriority) selectedPriority = 1;
            else if (priorityId == R.id.highPriority) selectedPriority = 2;
            
            // Durum filtresini ayarla
            if (statusId == R.id.allStatus) selectedStatus = -1;
            else if (statusId == R.id.completedStatus) selectedStatus = 1;
            else if (statusId == R.id.uncompletedStatus) selectedStatus = 0;
            
            loadTodos(); // Filtrelenmiş listeyi yükle
            dialog.dismiss();
        });
        
        dialog.show();
    }
} 