package com.example.budgettracker.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String type; // Cash, Bank, Card
    public double balance;

    public AccountEntity(String name, String type, double balance) {
        this.name = name;
        this.type = type;
        this.balance = balance;
    }
}
