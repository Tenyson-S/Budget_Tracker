package com.example.budgettracker.ui.analysis;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.data.repository.TransactionRepository;
import com.example.budgettracker.logic.CategoryAnalyzer;
import com.example.budgettracker.logic.CategorySpent;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnalysisViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final CategoryAnalyzer categoryAnalyzer;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<AnalysisResult> _analysisResult = new MutableLiveData<>();
    public final LiveData<AnalysisResult> analysisResult = _analysisResult;

    public AnalysisViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        categoryAnalyzer = new CategoryAnalyzer();
    }

    public void fetchAnalysisData(long start, long end) {
        executor.execute(() -> {
            List<TransactionEntity> transactions = repository.getTransactionForPeriod(start, end);
            
            double income = 0;
            double expense = 0;
            double savings = 0;

            for (TransactionEntity t : transactions) {
                if ("Income".equalsIgnoreCase(t.type)) {
                    income += t.amount;
                } else if ("EXPENSE".equalsIgnoreCase(t.type)) {
                     expense += t.amount;
                }
            }
            
            List<CategorySpent> categorySpents = prepareCategorySpents(transactions);
            // Calculate total for percentages
            double totalCategoryExpense = 0;
            for(CategorySpent c : categorySpents) totalCategoryExpense += c.amount;

            _analysisResult.postValue(new AnalysisResult(income, expense, savings, categorySpents, totalCategoryExpense));
        });
    }

    private List<CategorySpent> prepareCategorySpents(List<TransactionEntity> list) {
        java.util.List<CategorySpent> spends = new java.util.ArrayList<>();
        if (list == null) return spends;
        
        java.util.Map<String, Double> map = new java.util.HashMap<>();

        for(TransactionEntity t : list){
            if("EXPENSE".equalsIgnoreCase(t.type)) {
                map.put(t.category, map.getOrDefault(t.category, 0.0) + t.amount);
            }
        }
        
        for(java.util.Map.Entry<String, Double> entry : map.entrySet()){
            spends.add(new CategorySpent(entry.getKey(), entry.getValue()));
        }
        
        // Sort by amount desc
        spends.sort((o1, o2) -> Double.compare(o2.amount, o1.amount));

        return spends;
    }

    public static class AnalysisResult {
        public double income;
        public double expense;
        public double savings; 
        public List<CategorySpent> categorySpents;
        public double totalCategoryExpense;

        public AnalysisResult(double income, double expense, double savings, List<CategorySpent> categorySpents, double totalCategoryExpense) {
            this.income = income;
            this.expense = expense;
            this.savings = savings;
            this.categorySpents = categorySpents;
            this.totalCategoryExpense = totalCategoryExpense;
        }
    }
}
