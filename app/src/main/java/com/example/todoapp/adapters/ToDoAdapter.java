package com.example.todoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todoapp.R;
import com.example.todoapp.models.ToDo;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.widget.Toast;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {
    private List<ToDo> todos = new ArrayList<>();
    private final OnToDoClickListener listener;
    private final FirebaseFirestore db;

    public ToDoAdapter(OnToDoClickListener listener) {
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setTodos(List<ToDo> todos) {
        this.todos = todos;
        notifyDataSetChanged();
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
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToDoClick(todo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public interface OnToDoClickListener {
        void onToDoClick(ToDo todo);
    }

    class ToDoViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final TextView dueDateTextView;
        private final CheckBox checkBox;
        private final SimpleDateFormat dateFormat;
        private boolean isUserAction = true;

        ToDoViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.todoTitle);
            descriptionTextView = itemView.findViewById(R.id.todoDescription);
            dueDateTextView = itemView.findViewById(R.id.todoDueDate);
            checkBox = itemView.findViewById(R.id.todoCheckbox);
            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isUserAction) return;
                
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ToDo todo = todos.get(position);
                    todo.setCompleted(isChecked);
                    
                    db.collection("todos")
                        .document(todo.getId())
                        .update(
                            "completed", isChecked,
                            "updatedAt", todo.getUpdatedAt()
                        )
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(itemView.getContext(), 
                                "Görev durumu güncellendi", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            todo.setCompleted(!isChecked);
                            checkBox.setChecked(!isChecked);
                            Toast.makeText(itemView.getContext(), 
                                "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                }
            });
        }

        void bind(ToDo todo) {
            if (todo == null) return;
            
            isUserAction = false;
            
            if (titleTextView != null) {
                titleTextView.setText(todo.getTitle());
            }
            
            if (descriptionTextView != null) {
                descriptionTextView.setText(todo.getDescription());
            }
            
            if (dueDateTextView != null && todo.getDueDate() != null) {
                dueDateTextView.setText("Son Tarih: " + dateFormat.format(todo.getDueDate()));
            }
            
            if (checkBox != null) {
                checkBox.setChecked(todo.isCompleted());
            }
            
            // Önceliğe göre arka plan rengini ayarla
            int backgroundColor;
            switch (todo.getPriority()) {
                case 0: // Düşük
                    backgroundColor = itemView.getContext().getColor(R.color.priority_low);
                    break;
                case 1: // Orta
                    backgroundColor = itemView.getContext().getColor(R.color.priority_medium);
                    break;
                case 2: // Yüksek
                    backgroundColor = itemView.getContext().getColor(R.color.priority_high);
                    break;
                default:
                    backgroundColor = itemView.getContext().getColor(android.R.color.white);
                    break;
            }
            ((androidx.cardview.widget.CardView) itemView).setCardBackgroundColor(backgroundColor);
            
            isUserAction = true;
        }
    }
} 