package com.example.budgettracker.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.budgettracker.data.entity.TransactionEntity;
import com.example.budgettracker.data.dao.TransactionDao;

@Database(
        entities = {TransactionEntity.class, com.example.budgettracker.data.entity.AccountEntity.class},
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract TransactionDao transactionDao();
    public abstract com.example.budgettracker.data.dao.AccountDao accountDao();

    public static AppDatabase getINSTANCE(Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "budget_tracker_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }

        return INSTANCE;
    }

}
