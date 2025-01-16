package com.example.todoapp.views;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.adapters.ToDoAdapter;
import com.example.todoapp.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class CompletedTodosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView emptyMessage;
    private ImageButton backButton;
    private ToDoAdapter adapter;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_todos_activity);
        recyclerView = findViewById(R.id.completedRecyclerView);
        emptyMessage = findViewById(R.id.emptyMessage);
        backButton = findViewById(R.id.backButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ToDoAdapter(todo -> {
        });
        adapter.setCheckboxClickable(false);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();
        backButton.setOnClickListener(v -> {
            onBackPressed();
        });
        loadCompletedAndOverdueTodos();
    }
    private void loadCompletedAndOverdueTodos() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("todos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ToDo> completedAndOverdueTodos = new ArrayList<>();
                    Date now = new Date();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ToDo todo = doc.toObject(ToDo.class);
                        if (todo != null) {
                            if (todo.isCompleted() || (todo.getDueDate() != null && todo.getDueDate().before(now))) {
                                todo.setId(doc.getId());
                                completedAndOverdueTodos.add(todo);
                            }
                        }
                    }
                    if (completedAndOverdueTodos.isEmpty()) {
                        emptyMessage.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyMessage.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    adapter.setTodos(completedAndOverdueTodos);
                })
                .addOnFailureListener(e -> {
                    emptyMessage.setVisibility(View.VISIBLE);
                    emptyMessage.setText("Error loading data.");
                    recyclerView.setVisibility(View.GONE);
                });
    }
}