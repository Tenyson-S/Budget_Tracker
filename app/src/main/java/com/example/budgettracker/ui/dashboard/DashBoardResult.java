package com.example.budgettracker.ui.dashboard;

import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.logic.BudgetResult;
import com.example.budgettracker.logic.CategorySpent;
import com.example.budgettracker.logic.RecurringExpenseInsight;

import java.util.*;

public class DashBoardResult {
    public BudgetResult budgetResult;
    public CategorySpent highestCategorySpent;
    public Map<String, RecurringExpenseInsight> recurringExpenses;
    public List<TransactionEntity> transactions;

    public double totalIncome;
    public double totalExpenses;
    public double totalBalance;
    public double initialAmount; // Placeholder if needed

    public DashBoardResult(BudgetResult budgetResult,
                           CategorySpent highestCategorySpent,
                           Map<String, RecurringExpenseInsight> recurringExpenses,
                           List<TransactionEntity> transactions,
                           double totalIncome, double totalExpenses, double totalBalance){
        this.budgetResult = budgetResult;
        this.highestCategorySpent = highestCategorySpent;
        this.recurringExpenses = recurringExpenses;
        this.transactions = transactions;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.totalBalance = totalBalance;
    }
}
