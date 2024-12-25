package com.example.todoapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Splash ekranı için layout

        // 2 saniye bekledikten sonra yönlendirme
        new Handler().postDelayed(() -> {
            Intent intent;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Kullanıcı giriş yapmışsa MainActivity'ye yönlendir
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Kullanıcı giriş yapmamışsa LoginActivity'ye yönlendir
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // SplashActivity'yi kapat
        }, 2000); // 2 saniye bekleme süresi
    }
} 