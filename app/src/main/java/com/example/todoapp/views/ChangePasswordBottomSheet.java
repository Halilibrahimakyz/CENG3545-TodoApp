package com.example.todoapp.views;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.todoapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {
    private EditText oldPasswordInput, newPasswordInput, confirmPasswordInput;
    private Button updatePasswordButton;
    private FirebaseAuth auth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_change_password, container, false);
        auth = FirebaseAuth.getInstance();
        oldPasswordInput = view.findViewById(R.id.oldPasswordInput);
        newPasswordInput = view.findViewById(R.id.newPasswordInput);
        confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        updatePasswordButton = view.findViewById(R.id.updatePasswordButton);
        updatePasswordButton.setOnClickListener(v -> changePassword());
        return view;
    }
    private void changePassword() {
        String oldPassword = oldPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getContext(), "New passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.equals(newPassword)) {
            Toast.makeText(getContext(), "Old password and new password cannot be the same.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            if (email != null) {
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                auth.getCurrentUser().reauthenticate(credential)
                        .addOnSuccessListener(unused -> {
                            auth.getCurrentUser().updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Password updated successfully.", Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Password could not be updated: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Your old password is incorrect: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }
}