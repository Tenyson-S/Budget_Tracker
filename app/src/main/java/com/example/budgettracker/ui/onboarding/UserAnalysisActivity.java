package com.example.budgettracker.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgettracker.MainActivity;
import com.example.budgettracker.R;
import com.example.budgettracker.data.database.AppDatabase;
import com.example.budgettracker.data.entity.AccountEntity;
import com.example.budgettracker.data.entity.TransactionEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class UserAnalysisActivity extends AppCompatActivity {

    private TextInputEditText etIncome;
    private RecyclerView rvAccounts;
    private AccountSetupAdapter adapter;
    private List<AccountEntity> tempAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_analysis);

        etIncome = findViewById(R.id.etIncome);
        rvAccounts = findViewById(R.id.rvAccounts);
        MaterialButton btnAddAccount = findViewById(R.id.btnAddAccount);
        MaterialButton btnFinish = findViewById(R.id.btnFinish);

        // Default Accounts
        tempAccounts = new ArrayList<>();
        tempAccounts.add(new AccountEntity("Cash", "Cash", 0));
        tempAccounts.add(new AccountEntity("Bank Account", "Bank", 0));

        adapter = new AccountSetupAdapter(tempAccounts, account -> {
            tempAccounts.remove(account);
            adapter.notifyDataSetChanged();
        });

        rvAccounts.setLayoutManager(new LinearLayoutManager(this));
        rvAccounts.setAdapter(adapter);

        btnAddAccount.setOnClickListener(v -> showAddAccountDialog());
        btnFinish.setOnClickListener(v -> finishSetup());
    }

    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Account");

        final EditText input = new EditText(this);
        input.setHint("Account Name (e.g., Wallet, SBI)");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!TextUtils.isEmpty(name)) {
                tempAccounts.add(new AccountEntity(name, "General", 0));
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void finishSetup() {
        String incomeStr = etIncome.getText().toString().trim();
        if (TextUtils.isEmpty(incomeStr)) {
            etIncome.setError("Please enter your income");
            return;
        }
        
        if (tempAccounts.isEmpty()) {
            Toast.makeText(this, "Please add at least one account", Toast.LENGTH_SHORT).show();
            return;
        }

        double income = Double.parseDouble(incomeStr);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getINSTANCE(this);

            long firstAccountId = -1;
            
            // 1. Save Accounts
            for (int i = 0; i < tempAccounts.size(); i++) {
                AccountEntity acc = tempAccounts.get(i);
                long id = db.accountDao().insert(acc);
                if (i == 0) firstAccountId = id;
            }

            // 2. Insert Initial Income Transaction
            if (firstAccountId != -1) {
                TransactionEntity initialIncome = new TransactionEntity(
                        "Initial Income",
                        income,
                        "Income", 
                        "Salary", 
                        System.currentTimeMillis(),
                        "Initial Setup",
                        "Onboarding",
                        "Bank", 
                        (int) firstAccountId
                );
                db.transactionDao().insertTransaction(initialIncome);
            }

            // 3. Mark Onboarding Complete
            SharedPreferences prefs = getSharedPreferences("budget_prefs", MODE_PRIVATE);
            prefs.edit().putBoolean("onboarding_complete", true).apply();

            // 4. Navigate
            runOnUiThread(() -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        });
    }
    
    // Internal Adapter Class
    public static class AccountSetupAdapter extends RecyclerView.Adapter<AccountSetupAdapter.ViewHolder> {
        private final List<AccountEntity> list;
        private final OnDeleteListener listener;

        public interface OnDeleteListener {
            void onDelete(AccountEntity account);
        }

        public AccountSetupAdapter(List<AccountEntity> list, OnDeleteListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_setup, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AccountEntity item = list.get(position);
            holder.tvName.setText(item.name);
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            ImageView btnDelete;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvAccountName);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
