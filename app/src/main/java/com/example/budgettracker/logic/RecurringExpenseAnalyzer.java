package com.example.budgettracker.logic;
import com.example.budgettracker.data.entity.TransactionEntity;

import java.util.*;
public class RecurringExpenseAnalyzer {
    public Map<String, RecurringExpenseInsight> analyze(List<TransactionEntity> expenses){
        Map<String, Integer> countMap = new HashMap<>();
        Map<String, Double> amountMap = new HashMap<>();

        for(TransactionEntity e : expenses){
            String title = e.title;

            countMap.put(title, countMap.getOrDefault(title, 0) + 1);
            amountMap.put(title, amountMap.getOrDefault(title, 0.0) + e.amount);
        }

        Map<String, RecurringExpenseInsight> result = new HashMap<>();

        for(String title : countMap.keySet()){
            result.put(
                    title, new RecurringExpenseInsight(title, countMap.get(title), amountMap.get(title))
            );
        }

        return result;
    }
}
