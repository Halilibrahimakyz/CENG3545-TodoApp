package com.example.todoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.models.ToDo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {
    private List<ToDo> todos = new ArrayList<>();
    private final OnToDoClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnToDoClickListener {
        void onToDoClick(ToDo todo);
    }

    public ToDoAdapter(OnToDoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToDoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_todo, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoViewHolder holder, int position) {
        ToDo todo = todos.get(position);
        holder.bind(todo);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void setTodos(List<ToDo> todos) {
        this.todos = todos != null ? todos : new ArrayList<>();
        notifyDataSetChanged();
    }

    class ToDoViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleText;
        private final TextView descriptionText;
        private final TextView dueDateText;
        private final TextView priorityText;

        ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dueDateText = itemView.findViewById(R.id.dueDateText);
            priorityText = itemView.findViewById(R.id.priorityText);
        }

        void bind(ToDo todo) {
            if (todo != null) {
                titleText.setText(todo.getTitle());
                descriptionText.setText(todo.getDescription());
                
                if (todo.getDueDate() != null) {
                    dueDateText.setText(dateFormat.format(todo.getDueDate()));
                } else {
                    dueDateText.setText("No date set");
                }
                
                priorityText.setText(String.valueOf(todo.getPriority()));
                itemView.setOnClickListener(v -> listener.onToDoClick(todo));
            }
        }
    }
} 