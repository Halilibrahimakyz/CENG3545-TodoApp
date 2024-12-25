package com.example.todoapp.utils;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.todoapp.R;

public class ToolbarUtil {
    public static void setupToolbarWithBack(Activity activity, String title) {
        TextView toolbarTitle = activity.findViewById(R.id.toolbarTitle);
        ImageButton backButton = activity.findViewById(R.id.backButton);
        
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
        
        if (backButton != null) {
            backButton.setOnClickListener(v -> activity.onBackPressed());
        }
    }
} 