package com.example.budgettracker.ui.dashboard;

import com.example.budgettracker.data.entity.TransactionEntity;

public interface OnTransactionActionListener {
    void onEdit(TransactionEntity transaction);
    void onDelete(TransactionEntity transaction);
    void onTransactionLongClicked(TransactionEntity transaction);
}
