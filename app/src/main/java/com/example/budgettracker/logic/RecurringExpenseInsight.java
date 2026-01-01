package com.example.budgettracker.logic;

public class RecurringExpenseInsight {
    public String title;
    public int frequency;
    public double totalPrice;

    public RecurringExpenseInsight(String title, int frequency, double totalPrice){
        this.title = title;
        this.frequency = frequency;
        this.totalPrice = totalPrice;
    }
}
