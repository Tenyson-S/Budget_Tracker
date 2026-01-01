package com.example.budgettracker.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgettracker.data.entity.AccountEntity;

import java.util.List;

@Dao
public interface AccountDao {
    @Insert
    long insert(AccountEntity account);

    @Update
    void update(AccountEntity account);

    @Delete
    void delete(AccountEntity account);

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> getAllAccounts();

    @Query("SELECT * FROM accounts WHERE id = :id")
    AccountEntity getAccountById(int id);
}
