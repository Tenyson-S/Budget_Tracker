package com.example.budgettracker.logic;
import java.util.List;
public class CategoryAnalyzer {
    public CategorySpent findHighestSpendingCategory(List<CategorySpent> spends){
        if(spends == null || spends.isEmpty()){
            return null;
        }

        CategorySpent highest = spends.get(0);

        for(CategorySpent spend: spends){
            if(spend.amount > highest.amount){
                highest = spend;
            }
        }

        return highest;
    }
}
