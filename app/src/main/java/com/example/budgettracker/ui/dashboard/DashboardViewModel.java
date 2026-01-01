package com.example.budgettracker.ui.dashboard;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.data.repository.TransactionRepository;
import com.example.budgettracker.logic.RecurringExpenseAnalyzer;
import com.example.budgettracker.logic.RecurringExpenseInsight;
import com.example.budgettracker.logic.BudgetInput;
import com.example.budgettracker.logic.BudgetResult;
import com.example.budgettracker.logic.BudgetRuleEngine;
import com.example.budgettracker.logic.CategoryAnalyzer;
import com.example.budgettracker.logic.CategorySpent;

import java.util.*;

public class DashboardViewModel extends AndroidViewModel {
    private final TransactionRepository repository;
    private final CategoryAnalyzer categoryAnalyzer;
    private final RecurringExpenseAnalyzer recurringExpenseAnalyzer;
    private final BudgetRuleEngine budgetRuleEngine;

    private TransactionEntity lastDeletedTransaction;

    public DashboardViewModel(@NonNull Application application){
        super(application);
        repository = new TransactionRepository(application);

        categoryAnalyzer = new CategoryAnalyzer();
        recurringExpenseAnalyzer = new RecurringExpenseAnalyzer();
        budgetRuleEngine = new BudgetRuleEngine();
    }

    private final java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
    private final androidx.lifecycle.MutableLiveData<DashBoardResult> _result = new androidx.lifecycle.MutableLiveData<>();
    public final androidx.lifecycle.LiveData<DashBoardResult> result = _result;

    public void fetchDashboardData(long start, long end){
        executor.execute(() -> {
            List<TransactionEntity> transactions = repository.getTransactionForPeriod(start, end);

            BudgetInput input = prepareBudgetInput(transactions);
            BudgetResult budgetResult = budgetRuleEngine.evaluate(input);

            List<CategorySpent> categorySpents = prepareCategorySpents(transactions);
            CategorySpent highestCategory = categoryAnalyzer.findHighestSpendingCategory(categorySpents);

            Map<String, RecurringExpenseInsight> recurringMap = recurringExpenseAnalyzer.analyze(transactions);

            double totalExpenses = input.needsSpent + input.wantsSpent;
            // Balance = Income - Expenses (excluding savings as "expense" for display, or keeping it?
            // Dashboard requested "Left" = Balance. 
            // Total Balance = Income - (Expenses + Savings).
            double totalBalance = input.monthlyIncome - (totalExpenses + input.savingsAmount);

            DashBoardResult res = new DashBoardResult(
                    budgetResult, highestCategory, recurringMap, transactions,
                    input.monthlyIncome, totalExpenses, totalBalance
            );
            _result.postValue(res);
        });
    }

    private BudgetInput prepareBudgetInput(List<TransactionEntity> list){
        BudgetInput input = new BudgetInput();

        double income = 0;
        double needs = 0;
        double wants = 0;
        double savings = 0;

        if (list != null) {
            for(TransactionEntity t : list){
                if("Income".equalsIgnoreCase(t.type)){
                    income += t.amount;
                }
                else{
                    if("savings".equalsIgnoreCase(t.type)){
                        savings += t.amount;
                    }else if("Food".equalsIgnoreCase(t.type) || "rent".equalsIgnoreCase(t.type)){
                        needs += t.amount;
                    }else{
                        wants += t.amount;
                    }
                }
            }
        }

        input.monthlyIncome = income;
        input.needsSpent = needs;
        input.wantsSpent = wants;
        input.savingsAmount = savings;

        return input;
    }

    public void deleteTransaction(TransactionEntity transaction){
        repository.delete(transaction);
        lastDeletedTransaction = transaction;
    }

    public void undoDelete() {
        if (lastDeletedTransaction != null) {
            repository.insert(lastDeletedTransaction);
            lastDeletedTransaction = null;
        }
    }


    private List<CategorySpent> prepareCategorySpents(List<TransactionEntity> list) {
        List<CategorySpent> spends = new ArrayList<>();
        if (list == null) return spends;

        for(TransactionEntity t : list){
            if("EXPENSE".equalsIgnoreCase(t.type)) {
                spends.add(new CategorySpent(t.category, t.amount));
            }
        }
        return spends;
    }
}
