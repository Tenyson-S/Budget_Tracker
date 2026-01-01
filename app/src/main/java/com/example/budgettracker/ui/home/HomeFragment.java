package com.example.budgettracker.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.logic.RecurringExpenseInsight;
import com.example.budgettracker.ui.addtransaction.AddTransactionActivity;
import com.example.budgettracker.ui.dashboard.DashBoardResult;
import com.example.budgettracker.ui.dashboard.DashboardViewModel;
import com.example.budgettracker.ui.dashboard.OnTransactionActionListener;
import com.example.budgettracker.ui.dashboard.RecurringExpensesAdapter;
import com.example.budgettracker.ui.dashboard.SuggestionAdapter;
import com.example.budgettracker.ui.dashboard.TransactionAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements OnTransactionActionListener {

    private DashboardViewModel viewModel;
    private RecyclerView rvTransactions;
    private RecyclerView rvSuggestions;

    private TextView budgetStatusText;
    private TextView highestCategoryText;
    private TextView recurringText;
    private TextView tvSelectedMonth;

    private ProgressBar pbNeeds, pbWants, pbSavings;
    private TextView tvNeeds, tvWants, tvSavings;
    private TextView tvIncome, tvExpense, tvLeft, tvInitialAmount;

    private int currentMonth;
    private int currentYear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---------- Text Views ----------
        budgetStatusText = view.findViewById(R.id.budget_status_text);
        highestCategoryText = view.findViewById(R.id.highest_category_text);
        recurringText = view.findViewById(R.id.recurring_text);
        tvSelectedMonth = view.findViewById(R.id.tvSelectedMonth);

        // ---------- Progress ----------
        tvNeeds = view.findViewById(R.id.tvNeeds);
        tvWants = view.findViewById(R.id.tvWants);
        tvSavings = view.findViewById(R.id.tvSavings);

        tvIncome = view.findViewById(R.id.tvIncome);
        tvExpense = view.findViewById(R.id.tvExpense);
        tvLeft = view.findViewById(R.id.tvLeft);
        tvInitialAmount = view.findViewById(R.id.tvInitialAmount);

        pbNeeds = view.findViewById(R.id.pbNeeds);
        pbWants = view.findViewById(R.id.pbWants);
        pbSavings = view.findViewById(R.id.pbSavings);

        // ---------- RecyclerView ----------
        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        rvSuggestions = view.findViewById(R.id.rvSuggestions);
        rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext()));

        // ---------- ViewModel ----------
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Observer
        viewModel.result.observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            showResult(result);
            rvTransactions.setAdapter(
                    new TransactionAdapter(result.transactions, this)
            );
        });

        // ---------- FAB ----------
        FloatingActionButton fab = view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddTransactionActivity.class))
        );

        // ---------- Recurring Dialog ----------
        view.findViewById(R.id.recurring_text).setOnClickListener(v -> showRecurringDialog()); // Using parent view click for bigger target if needed, but text is fine

        // ---------- Month Init ----------
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH);
        currentYear = cal.get(Calendar.YEAR);

        updateMonthLabel();

        // ---------- Month Navigation ----------
        view.findViewById(R.id.btnPrevMonth).setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            c.set(currentYear, currentMonth, 1);
            c.add(Calendar.MONTH, -1);
            currentMonth = c.get(Calendar.MONTH);
            currentYear = c.get(Calendar.YEAR);
            updateMonthLabel();
            loadDashBoard();
        });

        view.findViewById(R.id.btnNextMonth).setOnClickListener(v -> {
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
    public void onResume() {
        super.onResume();
        loadDashBoard();
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

        // Dynamic Colors
        int colorRed = getResources().getColor(R.color.expense_red, requireContext().getTheme());
        int colorBlue = getResources().getColor(R.color.blue_primary, requireContext().getTheme());
        int colorGreen = getResources().getColor(R.color.income_green, requireContext().getTheme());

        pbNeeds.getProgressDrawable().setTint(result.budgetResult.needsExceeded ? colorRed : colorBlue);
        pbWants.getProgressDrawable().setTint(result.budgetResult.wantsExceeded ? colorRed : colorBlue);
        pbSavings.getProgressDrawable().setTint(result.budgetResult.savingsBelowTarget ? colorRed : colorGreen);

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
        if (getContext() == null) return;
        DashBoardResult result = viewModel.result.getValue();
        if (result == null || result.recurringExpenses == null || result.recurringExpenses.isEmpty()) return;

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_recurring_expenses, null);

        RecyclerView rv = view.findViewById(R.id.rvRecurring);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RecurringExpenseInsight> list =
                new ArrayList<>(result.recurringExpenses.values());

        rv.setAdapter(new RecurringExpensesAdapter(list));

        new AlertDialog.Builder(getContext())
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
        Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
        intent.putExtra("transaction_id", transaction.id);
        startActivity(intent);
    }

    @Override
    public void onDelete(TransactionEntity transaction) {
        showDeleteConfirmation(transaction);
    }

    @Override
    public void onTransactionLongClicked(TransactionEntity transaction) {
        if (getContext() == null) return;
        String[] options = {getString(R.string.edit), getString(R.string.delete)};

        new AlertDialog.Builder(getContext())
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

    private void showDeleteConfirmation(TransactionEntity transaction) {
        if (getContext() == null) return;
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete_transaction)
                .setMessage(R.string.delete_confirm_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteTransaction(transaction);
                    loadDashBoard();
                    
                    if (getView() != null) {
                        Snackbar.make(
                                getView(),
                                R.string.transaction_deleted,
                                Snackbar.LENGTH_LONG
                        ).setAction(R.string.undo, v -> {
                            viewModel.undoDelete();
                            loadDashBoard();
                        }).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
