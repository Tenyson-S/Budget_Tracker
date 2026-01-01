package com.example.budgettracker.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettracker.R;
import com.example.budgettracker.ui.dashboard.DashBoardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for 2 seconds then move to Dashboard
        // Delay for 2 seconds then move to Dashboard or Onboarding
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            android.content.SharedPreferences prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE);
            boolean isComplete = prefs.getBoolean("onboarding_complete", false);

            Intent intent;
            if (isComplete) {
                intent = new Intent(SplashActivity.this, com.example.budgettracker.MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, com.example.budgettracker.ui.onboarding.OnboardingActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
