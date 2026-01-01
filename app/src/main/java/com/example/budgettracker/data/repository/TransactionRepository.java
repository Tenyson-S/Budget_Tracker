package com.example.budgettracker.data.repository;

import android.content.Context;

import com.example.budgettracker.data.dao.TransactionDao;
import com.example.budgettracker.data.database.AppDatabase;
import com.example.budgettracker.data.entity.TransactionEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final com.example.budgettracker.data.dao.AccountDao accountDao;
    private final ExecutorService executorService;

    public TransactionRepository(Context context) {
        AppDatabase database = AppDatabase.getINSTANCE(context);
        transactionDao = database.transactionDao();
        accountDao = database.accountDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(TransactionEntity transaction) {
        executorService.execute(() -> transactionDao.insertTransaction(transaction));
    }

    public List<TransactionEntity> getAllTransaction() {
        Callable<List<TransactionEntity>> task =
                () -> transactionDao.getAllTransactions();

        Future<List<TransactionEntity>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<TransactionEntity> getTransactionForPeriod(long start, long end) {
        Callable<List<TransactionEntity>> task =
                () -> transactionDao.getAllTransactionsByPeriod(start, end);

        Future<List<TransactionEntity>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void update(TransactionEntity transaction) {
        executorService.execute(() ->
                transactionDao.updateTransaction(transaction)
        );
    }

    public void delete(TransactionEntity transaction) {
        executorService.execute(() ->
                transactionDao.deleteTransaction(transaction)
        );
    }

    public void deleteAll() {
        executorService.execute(transactionDao::deleteAll);
    }

    public TransactionEntity getTransactionById(int id) {
        Callable<TransactionEntity> task = () -> transactionDao.getTransactionById(id);
        Future<TransactionEntity> future = executorService.submit(task);
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    public List<TransactionEntity> getExpensesByCategory(String category) {
        Callable<List<TransactionEntity>> task =
                () -> transactionDao.getExpensesByCategory(category);

        Future<List<TransactionEntity>> future = executorService.submit(task);

        try {
            return future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public androidx.lifecycle.LiveData<List<com.example.budgettracker.data.entity.AccountEntity>> getAllAccounts() {
        return accountDao.getAllAccounts();
    }

    public com.example.budgettracker.data.entity.AccountEntity getAccountById(int id) {
        Callable<com.example.budgettracker.data.entity.AccountEntity> task = () -> accountDao.getAccountById(id);
        Future<com.example.budgettracker.data.entity.AccountEntity> future = executorService.submit(task);
        try {
            return future.get();
        } catch (Exception e) {
            return null;
        }
    }

    public void updateAccount(com.example.budgettracker.data.entity.AccountEntity account) {
        executorService.execute(() -> accountDao.update(account));
    }

    public void insertAccount(com.example.budgettracker.data.entity.AccountEntity account) {
        executorService.execute(() -> accountDao.insert(account));
    }

    public void deleteAccount(com.example.budgettracker.data.entity.AccountEntity account) {
        executorService.execute(() -> accountDao.delete(account));
    }
}
