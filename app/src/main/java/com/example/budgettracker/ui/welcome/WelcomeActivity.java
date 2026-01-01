package com.example.budgettracker.ui.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettracker.R;
import com.example.budgettracker.ui.dashboard.DashBoardActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, DashBoardActivity.class));
            finish();
        }, 1500); // 1.5 seconds
    }
}
