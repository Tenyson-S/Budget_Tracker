package com.example.budgettracker.ui.addtransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgettracker.R;
import com.example.budgettracker.data.entity.AccountEntity;

import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etTitle, etAmount, etNotes, etTags;
    private RadioGroup rgType;
    private AutoCompleteTextView spCategory, spPaymentMode, spAccount;
    private AddTransactionViewModel viewModel;

    private List<AccountEntity> accountList;

    private final String[] expenseCategories = {
            "Food", "Travel", "Tea/Coffee",
            "Shopping", "Bills", "Rent", "Entertainment", "Others"
    };

    private final String[] incomeCategories = {
            "Salary", "Freelance", "Business",
            "Gift", "Interest", "Other Income"
    };

    private final String[] paymentModes = {
            "Cash", "Credit Card", "Debit Card",
            "UPI", "Net Banking", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        // Edge to Edge
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        viewModel = new ViewModelProvider(this).get(AddTransactionViewModel.class);

        setupSpinners();
        setupObservers();

        findViewById(R.id.btnSave).setOnClickListener(v -> saveTransaction());
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);
        etTags = findViewById(R.id.etTags);
        rgType = findViewById(R.id.rgType);
        spCategory = findViewById(R.id.spCategory);
        spPaymentMode = findViewById(R.id.spPaymentMode);
        spAccount = findViewById(R.id.spAccount);
    }

    private void setupSpinners() {
        setSpinner(spPaymentMode, paymentModes);
        
        // Default category adapter (Expense initially)
        setSpinner(spCategory, expenseCategories);

        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton rb = findViewById(checkedId);
            if (rb != null) {
                if ("Income".equalsIgnoreCase(rb.getText().toString())) {
                    setSpinner(spCategory, incomeCategories);
                } else {
                    setSpinner(spCategory, expenseCategories);
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getAccounts().observe(this, accounts -> {
            if (accounts != null) {
                accountList = accounts;
                String[] names = new String[accounts.size()];
                for (int i = 0; i < accounts.size(); i++) {
                    names[i] = accounts.get(i).name;
                }
                setSpinner(spAccount, names);
                
                // Pre-select first if available
                if (names.length > 0) {
                    spAccount.setText(names[0], false);
                }
            }
        });
    }

    private void setSpinner(AutoCompleteTextView spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);
        spinner.setAdapter(adapter);
    }

    private void saveTransaction() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required");
            return;
        }

        double amount = Double.parseDouble(amountStr);

        int selectedId = rgType.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String type = (rb != null) ? rb.getText().toString() : "Expense"; // Default or Error?
        if("Expense".equalsIgnoreCase(type)) type = "EXPENSE"; // Normalize
        if("Income".equalsIgnoreCase(type)) type = "Income";

        String category = spCategory.getText().toString();
        String paymentMode = spPaymentMode.getText().toString();
        String notes = etNotes.getText().toString();
        String tags = etTags.getText().toString();
        String accountName = spAccount.getText().toString();

        if (TextUtils.isEmpty(category)) category = "Others";
        
        int accountId = -1;
        if (accountList != null) {
            for (AccountEntity acc : accountList) {
                if (acc.name.equals(accountName)) {
                    accountId = acc.id;
                    break;
                }
            }
        }
        
        // If accountId is still -1, maybe create default or warn? 
        // Proceeding with -1 might crash constraint if foreign key exists?
        // AppDatabase entities usually have foreign keys.
        // Assuming user must select account.
        
        if (accountId == -1 && accountList != null && !accountList.isEmpty()) {
             // Fallback to first?
             accountId = accountList.get(0).id;
        }

        viewModel.saveTransaction(title, amount, type, category, notes, tags, paymentMode, accountId);
        Toast.makeText(this, "Transaction Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
