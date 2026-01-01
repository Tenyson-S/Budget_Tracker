package com.example.budgettracker.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.budgettracker.MainActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.ui.dashboard.DashBoardActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Initialize ViewPager and Adapter
        setupOnboardingItems();

        ViewPager2 onboardingViewPager = findViewById(R.id.viewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        // Buttons
        MaterialButton btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);

        btnSkip.setOnClickListener(v -> finishOnboarding());
        btnNext.setOnClickListener(v -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });
        
        // Update "Next" text to "Get Started" on last page
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == onboardingAdapter.getItemCount() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        onboardingItems.add(new OnboardingItem(
                "Track Expenses Easily",
                "Keep track of your daily spending with just a few taps. No complications.",
                R.mipmap.ic_launcher // Placeholder
        ));

        onboardingItems.add(new OnboardingItem(
                "Automatic Analysis",
                "Your expenses are automatically categorized to help you understand your habits.",
                R.mipmap.ic_launcher
        ));

        onboardingItems.add(new OnboardingItem(
                "50/30/20 Rule",
                "We organize your budget into Needs, Wants, and Savings for better financial health.",
                R.mipmap.ic_launcher
        ));
        
        onboardingItems.add(new OnboardingItem(
                "Recurring Insights",
                "Never miss a subscription or bill. We highlight recurring payments for you.",
                R.mipmap.ic_launcher
        ));

        onboardingItems.add(new OnboardingItem(
                "Privacy First",
                "Your data stays offline on your device. Secure and private by default.",
                R.mipmap.ic_launcher
        ));

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void finishOnboarding() {
        // Navigate to UserAnalysisActivity
        startActivity(new Intent(this, UserAnalysisActivity.class));
        finish();
    }
}
