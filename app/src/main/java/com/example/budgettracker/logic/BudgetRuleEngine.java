package com.example.budgettracker.logic;

public class BudgetRuleEngine {
    public BudgetResult evaluate(BudgetInput input){
        double income = input.monthlyIncome;
        double needsSpent = input.needsSpent;
        double wantsSpent = input.wantsSpent;
        double savings = income - (needsSpent + wantsSpent);

        BudgetResult result = new BudgetResult();

// existing logic
        result.needsLimit = income * 0.5;
        result.wantsLimit = income * 0.3;
        result.savingsTarget = income * 0.2;

        result.needsExceeded = needsSpent > result.needsLimit;
        result.wantsExceeded = wantsSpent > result.wantsLimit;
        result.savingsBelowTarget = savings < result.savingsTarget;

// 🔥 PERCENT CALCULATION (NEW)
        result.needsPercent = income == 0 ? 0 : (int) ((needsSpent / income) * 100);
        result.wantsPercent = income == 0 ? 0 : (int) ((wantsSpent / income) * 100);
        result.savingsPercent = 100 - (result.needsPercent + result.wantsPercent);


        java.util.List<String> suggestions = new java.util.ArrayList<>();

        if (result.needsExceeded) {
            double excess = needsSpent - result.needsLimit;
            suggestions.add(String.format("⚠ Needs exceeded by %.0f. Review fixed expenses.", excess));
        } else {
             double remaining = result.needsLimit - needsSpent;
            suggestions.add(String.format("✅ Needs under control. You have %.0f left.", remaining));
        }

        if (result.wantsExceeded) {
             double excess = wantsSpent - result.wantsLimit;
            suggestions.add(String.format("⚠ Wants exceeded by %.0f. Cut down on non-essentials.", excess));
        } else {
             double remaining = result.wantsLimit - wantsSpent;
            suggestions.add(String.format("👍 Wants within limit. You can spend %.0f more.", remaining));
        }

        if (result.savingsBelowTarget) {
            double shortage = result.savingsTarget - savings;
            suggestions.add(String.format("💡 You are %.0f short of your savings goal.", shortage));
        } else {
            suggestions.add("🎉 Savings target reached! Great job.");
        }

        result.suggestionList = suggestions;

        return result;

    }
}
