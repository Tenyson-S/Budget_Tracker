package com.example.budgettracker.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.logic.RecurringExpenseInsight;
import com.example.budgettracker.ui.addtransaction.AddTransactionActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashBoardActivity extends AppCompatActivity implements OnTransactionActionListener {

    private DashboardViewModel viewModel;
    // private DashBoardResult lastResult; // Removed field

    private RecyclerView rvTransactions;
    private RecyclerView rvSuggestions;

    private TextView budgetStatusText;
    private TextView highestCategoryText;
    private TextView recurringText;
// field removed

    private ProgressBar pbNeeds, pbWants, pbSavings;
    private TextView tvNeeds, tvWants, tvSavings;
    private TextView tvIncome, tvExpense, tvLeft, tvInitialAmount;

    private TextView tvSelectedMonth;

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Edge to Edge
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------- Text Views ----------
        budgetStatusText = findViewById(R.id.budget_status_text);
        highestCategoryText = findViewById(R.id.highest_category_text);
        recurringText = findViewById(R.id.recurring_text);
// field removed
        tvSelectedMonth = findViewById(R.id.tvSelectedMonth);

        // ---------- Progress ----------
        tvNeeds = findViewById(R.id.tvNeeds);
        tvWants = findViewById(R.id.tvWants);
        tvSavings = findViewById(R.id.tvSavings);

        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvLeft = findViewById(R.id.tvLeft);
        tvInitialAmount = findViewById(R.id.tvInitialAmount);

        pbNeeds = findViewById(R.id.pbNeeds);
        pbWants = findViewById(R.id.pbWants);
        pbSavings = findViewById(R.id.pbSavings);

        // ---------- RecyclerView ----------
        rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        rvSuggestions = findViewById(R.id.rvSuggestions);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(this));

        // ---------- ViewModel ----------
        viewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(DashboardViewModel.class);

        // Observer
        viewModel.result.observe(this, result -> {
            if (result == null) return;
            showResult(result);
            rvTransactions.setAdapter(
                    new TransactionAdapter(result.transactions, this)
            );
        });

        // ---------- FAB ----------
        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, AddTransactionActivity.class))
        );

        // ---------- Recurring Dialog ----------
        recurringText.setOnClickListener(v -> showRecurringDialog());

        // ---------- Month Init ----------
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        updateMonthLabel();

        // ---------- Month Navigation ----------
        findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.set(currentYear, currentMonth, 1);
            c.add(Calendar.MONTH, -1);
            currentMonth = c.get(Calendar.MONTH);
            currentYear = c.get(Calendar.YEAR);
            updateMonthLabel();
            loadDashBoard();
        });

        findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.set(currentYear, currentMonth, 1);
            c.add(Calendar.MONTH, 1);
            currentMonth = c.get(Calendar.MONTH);
            currentYear = c.get(Calendar.YEAR);
            updateMonthLabel();
            loadDashBoard();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashBoard();
    }

    private void showDeleteConfirmation(TransactionEntity transaction) {

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_transaction)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {

                    viewModel.deleteTransaction(transaction);
                    loadDashBoard();

                    Snackbar.make(
                            findViewById(R.id.rootLayout),
                            R.string.transaction_deleted,
                            Snackbar.LENGTH_LONG
                    ).setAction(R.string.undo, v -> {
                        viewModel.undoDelete();
                        loadDashBoard();
                    }).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void loadDashBoard() {
        long start = getMonthStartMillis();
        long end = getMonthEndMillis();
        viewModel.fetchDashboardData(start, end);
    }

    private void showResult(DashBoardResult result) {
        if (result.budgetResult == null) return;

        budgetStatusText.setText(
                getString(
                        result.budgetResult.needsExceeded
                                ? R.string.needs_exceeded
                                : R.string.needs_ok
                )
        );

        if (result.highestCategorySpent != null) {
            highestCategoryText.setText(
                    getString(
                            R.string.highest_category,
                            result.highestCategorySpent.category,
                            result.highestCategorySpent.amount
                    )
            );
        } else {
            highestCategoryText.setText(getString(R.string.no_expenses));
        }

        recurringText.setText(
                getString(R.string.recurring_items, 
                        result.recurringExpenses != null ? result.recurringExpenses.size() : 0)
        );

        pbNeeds.setProgress(result.budgetResult.needsPercent);
        pbWants.setProgress(result.budgetResult.wantsPercent);
        pbSavings.setProgress(result.budgetResult.savingsPercent);

        tvNeeds.setText(
                getString(R.string.needs_percent, result.budgetResult.needsPercent)
        );
        tvWants.setText(
                getString(R.string.wants_percent, result.budgetResult.wantsPercent)
        );
        tvSavings.setText(
                getString(R.string.savings_percent, result.budgetResult.savingsPercent)
        );

        if (result.budgetResult.suggestionList != null) {
            rvSuggestions.setAdapter(new SuggestionAdapter(result.budgetResult.suggestionList));
        }

        // Update Summary texts
        tvIncome.setText(String.format(Locale.getDefault(), "₹%.2f", result.totalIncome));
        tvExpense.setText(String.format(Locale.getDefault(), "₹%.2f", result.totalExpenses));
        tvLeft.setText(String.format(Locale.getDefault(), "₹%.2f", result.totalBalance));
        tvInitialAmount.setText(R.string.initial_rupee);
    }

    private void showRecurringDialog() {
        DashBoardResult result = viewModel.result.getValue();
        if (result == null || result.recurringExpenses == null || result.recurringExpenses.isEmpty()) return;

        View view = getLayoutInflater()
                .inflate(R.layout.dialog_recurring_expenses, null);

        RecyclerView rv = view.findViewById(R.id.rvRecurring);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<RecurringExpenseInsight> list =
                new ArrayList<>(result.recurringExpenses.values());

        rv.setAdapter(new RecurringExpensesAdapter(list));

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(R.string.close, null)
                .show();
    }

    private void updateMonthLabel() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, currentMonth);
        cal.set(Calendar.YEAR, currentYear);

        String label = new SimpleDateFormat(
                "MMMM yyyy", Locale.getDefault()
        ).format(cal.getTime());

        tvSelectedMonth.setText(label);
    }

    private long getMonthStartMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getMonthEndMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth,
                cal.getActualMaximum(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTimeInMillis();
    }

    @Override
    public void onEdit(TransactionEntity transaction) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("transaction_id", transaction.id);
        startActivity(intent);
    }

    @Override
    public void onDelete(TransactionEntity transaction) {
        showDeleteConfirmation(transaction);
    }
    @Override
    public void onTransactionLongClicked(TransactionEntity transaction) {
        String[] options = {getString(R.string.edit), getString(R.string.delete)};

        new AlertDialog.Builder(this)
                .setTitle(R.string.options)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        onEdit(transaction);
                    } else {
                        onDelete(transaction);
                    }
                })
                .show();
    }
}
