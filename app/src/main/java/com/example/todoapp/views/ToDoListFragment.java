package com.example.todoapp.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.adapters.ToDoAdapter;
import com.example.todoapp.callbacks.ToDoListCallback;
import com.example.todoapp.controllers.ToDoController;
import com.example.todoapp.models.ToDo;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class ToDoListFragment extends Fragment {
    private RecyclerView todoRecyclerView;
    private ToDoAdapter todoAdapter;
    private ToDoController todoController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        todoController = new ToDoController();
        initViews(view);
        setupRecyclerView();
        
        // Sadece kullanıcı giriş yapmışsa ToDo'ları getir
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadTodos();
        }
    }

    private void initViews(View view) {
        todoRecyclerView = view.findViewById(R.id.todoRecyclerView);
    }

    private void setupRecyclerView() {
        todoAdapter = new ToDoAdapter(todo -> {
            // ToDo öğesine tıklandığında
            // Detay sayfasına yönlendirme yapılabilir
        });
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        todoRecyclerView.setAdapter(todoAdapter);
    }

    private void loadTodos() {
        todoController.getToDos(false, new ToDoListCallback() {
            @Override
            public void onSuccess(List<ToDo> todos) {
                if (getActivity() != null) {
                    todoAdapter.setTodos(todos);
                }
            }

            @Override
            public void onError(String error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Failed to load todos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
} 