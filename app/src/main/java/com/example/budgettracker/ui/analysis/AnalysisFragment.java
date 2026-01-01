package com.example.budgettracker.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AnalysisFragment extends Fragment {

    private AnalysisViewModel viewModel;
    private CategoryAnalysisAdapter adapter;
    private Calendar currentMonth;

    private TextView tvMonth, tvTotalExpense, tvTotalIncome;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(AnalysisViewModel.class);
        currentMonth = Calendar.getInstance();

        initViews(view);
        setupObservers();

        loadData();
    }

    private void initViews(View view) {
        tvMonth = view.findViewById(R.id.tvSelectedMonth);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        
        RecyclerView rv = view.findViewById(R.id.rvAnalysis);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAnalysisAdapter();
        rv.setAdapter(adapter);

        ImageButton btnPrev = view.findViewById(R.id.btnPrevMonth);
        ImageButton btnNext = view.findViewById(R.id.btnNextMonth);

        btnPrev.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            loadData();
        });

        btnNext.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            loadData();
        });
    }

    private void setupObservers() {
        viewModel.analysisResult.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            
            tvTotalIncome.setText(String.format("₹%.0f", result.income));
            tvTotalExpense.setText(String.format("₹%.0f", result.expense));
            
            adapter.submitList(result.categorySpents, result.totalCategoryExpense);
        });
    }

    private void loadData() {
        updateMonthLabel();
        
        // Month start
        Calendar start = (Calendar) currentMonth.clone();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);

        // Month end
        Calendar end = (Calendar) currentMonth.clone();
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        viewModel.fetchAnalysisData(start.getTimeInMillis(), end.getTimeInMillis());
    }

    private void updateMonthLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonth.setText(sdf.format(currentMonth.getTime()));
    }
}
