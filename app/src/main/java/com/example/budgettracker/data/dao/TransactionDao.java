package com.example.budgettracker.data.dao;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.budgettracker.data.entity.TransactionEntity;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insertTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();

    @Query("SELECT * FROM transactions WHERE date between :startDate and :endDate ORDER BY date DESC")
    List<TransactionEntity> getAllTransactionsByPeriod(long startDate, long endDate);


    @Query("Select * FROM transactions WHERE category = :category and type = 'EXPENSE'")
    List<TransactionEntity> getExpensesByCategory(String category);

    @Update
    void updateTransaction(TransactionEntity transaction);

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    TransactionEntity getTransactionById(int id);


    @Delete
    void deleteTransaction(TransactionEntity transaction);

    @Query("DELETE FROM transactions")
    void deleteAll();
}

