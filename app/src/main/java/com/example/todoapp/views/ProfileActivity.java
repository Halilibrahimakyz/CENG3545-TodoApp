package com.example.todoapp.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.todoapp.R;
import com.example.todoapp.utils.ToolbarUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 101;

    private ImageView profileImageView;
    private TextView emailText, currentTasksCount, completedTasksCount, incompleteTasksCount, changePasswordText;
    private Button logoutButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Uri selectedImageUri;
    private boolean isProfileImageSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ToolbarUtil.setupToolbarWithBack(this, "Profile");

        initViews();

        loadUserData();

        refreshMediaScanner();

        setupListeners();
    }

    private void initViews() {
        profileImageView = findViewById(R.id.profileImageView);
        emailText = findViewById(R.id.emailText);
        currentTasksCount = findViewById(R.id.currentTasksCount);
        completedTasksCount = findViewById(R.id.completedTasksCount);
        incompleteTasksCount = findViewById(R.id.incompleteTasksCount);
        changePasswordText = findViewById(R.id.changePasswordText);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupListeners() {
        profileImageView.setOnClickListener(v -> showProfileImageOptions());
        changePasswordText.setOnClickListener(v -> {
            ChangePasswordBottomSheet bottomSheet = new ChangePasswordBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "ChangePasswordBottomSheet");
        });
        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                emailText.setText(email);
            } else {
                emailText.setText("Email not found");
            }
            loadTaskData(user);
            loadProfileImage();
        }
    }

    private void loadTaskData(FirebaseUser user) {
        String userId = user.getUid();
        db.collection("todos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int currentTasks = 0;
                    int completedTasks = 0;
                    int incompleteAndOverdueTasks = 0;

                    Date now = new Date();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Boolean isCompleted = doc.getBoolean("completed");
                        Date dueDate = doc.getDate("dueDate");

                        if (isCompleted != null && isCompleted) {
                            completedTasks++;
                        } else {
                            if (dueDate != null && dueDate.before(now)) {
                                incompleteAndOverdueTasks++;
                            } else {
                                currentTasks++;
                            }
                        }
                    }

                    currentTasksCount.setText(String.valueOf(currentTasks));
                    completedTasksCount.setText(String.valueOf(completedTasks));
                    incompleteTasksCount.setText(String.valueOf(incompleteAndOverdueTasks));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error loading task data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProfileImage() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            Picasso.get().load(user.getPhotoUrl()).into(profileImageView);
            isProfileImageSet = true;
        } else {
            profileImageView.setImageResource(R.drawable.ic_profile);
            isProfileImageSet = false;
        }
    }

    private void showProfileImageOptions() {
        String[] options = {"Add/Change Profile Image", "Remove Profile Image"};

        new AlertDialog.Builder(this)
                .setTitle("Profile Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        checkAndRequestPermissions();
                    } else if (which == 1) {
                        if (isProfileImageSet) {
                            removeProfileImage();
                        } else {
                            Toast.makeText(this, "Profile image already removed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void checkAndRequestPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_IMAGE_PICK);
            } else {
                openImagePicker();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_PICK);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void removeProfileImage() {
        profileImageView.setImageResource(R.drawable.ic_profile);
        isProfileImageSet = false;
        Toast.makeText(this, "Profile image removed.", Toast.LENGTH_SHORT).show();
    }

    private void refreshMediaScanner() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse("file:///sdcard/DCIM/");
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                profileImageView.setImageURI(selectedImageUri);
                isProfileImageSet = true;
                Toast.makeText(this, "Profile image updated.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_PICK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Gallery access denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}