package com.example.budgettracker.ui.accounts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgettracker.data.entity.AccountEntity;
import com.example.budgettracker.data.repository.TransactionRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountsViewModel extends AndroidViewModel {

    private final TransactionRepository repository;
    private final ExecutorService executorService;
    private final LiveData<List<AccountEntity>> allAccounts;

    public AccountsViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        allAccounts = repository.getAllAccounts();
    }

    public LiveData<List<AccountEntity>> getAllAccounts() {
        return allAccounts;
    }

    public void addAccount(String name, String type) {
        repository.insertAccount(new AccountEntity(name, type, 0));
    }

    public void deleteAccount(AccountEntity account) {
        repository.deleteAccount(account);
    }
}
