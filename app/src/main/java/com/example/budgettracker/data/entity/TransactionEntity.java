package com.example.budgettracker.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class TransactionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public double amount;
    public String type;
    public String category;
    public long date;

    // ✅ Constructor used by app
    public TransactionEntity(
            String title,
            double amount,
            String type,
            String category,
            long date,
            String notes,
            String tags,
            String paymentMode,
            int accountId
    ) {
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
        this.notes = notes;
        this.tags = tags;
        this.paymentMode = paymentMode;
        this.accountId = accountId;
    }

    public String notes;
    public String tags;
    public String paymentMode;
    public int accountId;
}
