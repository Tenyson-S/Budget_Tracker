package com.example.budgettracker.ui.addtransaction;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.budgettracker.data.entity.AccountEntity;
import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.data.repository.TransactionRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddTransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final ExecutorService executorService;

    private final MutableLiveData<TransactionEntity> _transactionData = new MutableLiveData<>();
    public LiveData<TransactionEntity> transactionData = _transactionData;

    private boolean isEditMode = false;
    private int editingTransactionId = -1;

    public AddTransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void loadTransaction(int id) {
        editingTransactionId = id;
        isEditMode = true;
        executorService.execute(() -> {
            TransactionEntity entity = repository.getTransactionById(id);
            _transactionData.postValue(entity);
        });
    }

    public LiveData<List<AccountEntity>> getAccounts() {
        return repository.getAllAccounts();
    }

    public void saveTransaction(String title, double amount, String type, String category, String notes, String tags, String paymentMode, int accountId) {
        long timestamp = System.currentTimeMillis();

        executorService.execute(() -> {
            if (isEditMode) {
                TransactionEntity updateEntity = new TransactionEntity(
                        title, amount, type, category, timestamp, notes, tags, paymentMode, accountId
                );
                updateEntity.id = editingTransactionId;
                repository.update(updateEntity);
            } else {
                TransactionEntity newEntity = new TransactionEntity(
                        title, amount, type, category, timestamp, notes, tags, paymentMode, accountId
                );
                repository.insert(newEntity);

            }
            
            // Update Account Balance
            if (accountId != -1) {
                AccountEntity account = repository.getAccountById(accountId);
                if (account != null) {
                    if ("Income".equalsIgnoreCase(type)) {
                        account.balance += amount;
                    } else if ("Expense".equalsIgnoreCase(type)) {
                        account.balance -= amount;
                    }
                    // Transfer logic would require From/To, which we haven't implemented fully in UI yet.
                    // Assuming basic Income/Expense for now.
                    repository.updateAccount(account);
                }
            }
        });
    }
}
