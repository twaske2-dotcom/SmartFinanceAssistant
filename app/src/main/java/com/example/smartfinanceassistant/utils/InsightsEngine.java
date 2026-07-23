package com.example.smartfinanceassistant.utils;

import com.example.smartfinanceassistant.models.Budget;
import com.example.smartfinanceassistant.models.Expense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsightsEngine {

    private List<Expense> expenses;
    private List<Budget> budgets;

    public InsightsEngine(List<Expense> expenses, List<Budget> budgets) {
        this.expenses = expenses;
        this.budgets = budgets;
    }

    // Generate all insights
    public List<String> generateInsights() {
        List<String> insights = new ArrayList<>();

        insights.addAll(getCategoryInsights());
        insights.addAll(getBudgetInsights());
        insights.addAll(getSavingsTips());

        if (insights.isEmpty()) {
            insights.add("Keep tracking your expenses to get smart insights!");
        }

        return insights;
    }

    // Find top spending category
    private List<String> getCategoryInsights() {
        List<String> insights = new ArrayList<>();
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense e : expenses) {
            if (e.type.equals("expense")) {
                categoryTotals.merge(e.category, e.amount, Double::sum);
            }
        }

        if (!categoryTotals.isEmpty()) {
            String topCategory = Collections.max(
                    categoryTotals.entrySet(),
                    Map.Entry.comparingByValue()
            ).getKey();

            double topAmount = categoryTotals.get(topCategory);
            insights.add("💸 Highest spending: " + topCategory
                    + " (₹" + String.format("%.0f", topAmount) + ")");

            // Check if any category is unusually high
            double totalSpending = 0;
            for (double val : categoryTotals.values()) totalSpending += val;

            for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
                double percentage = (entry.getValue() / totalSpending) * 100;
                if (percentage > 40) {
                    insights.add("⚠️ " + entry.getKey()
                            + " is " + String.format("%.0f", percentage)
                            + "% of total spending!");
                }
            }
        }

        return insights;
    }

    // Budget warnings
    private List<String> getBudgetInsights() {
        List<String> insights = new ArrayList<>();

        for (Budget budget : budgets) {
            int percent = budget.getPercentageUsed();

            if (percent >= 100) {
                insights.add("🔴 " + budget.category
                        + " budget EXCEEDED by ₹"
                        + String.format("%.0f", budget.spent - budget.monthlyLimit));
            } else if (percent >= 80) {
                insights.add("🟡 " + budget.category
                        + " budget is " + percent + "% used. Be careful!");
            } else if (percent < 30) {
                insights.add("🟢 Great job! " + budget.category
                        + " budget is well managed.");
            }
        }

        return insights;
    }

    // Savings tips
    private List<String> getSavingsTips() {
        List<String> tips = new ArrayList<>();

        double totalExpense = 0;
        double totalIncome = 0;

        for (Expense e : expenses) {
            if (e.type.equals("expense")) totalExpense += e.amount;
            else totalIncome += e.amount;
        }

        if (totalIncome > 0) {
            double savingsRate = ((totalIncome - totalExpense) / totalIncome) * 100;

            if (savingsRate < 20) {
                tips.add("💡 Tip: Try to save at least 20% of income. " +
                        "Currently saving " + String.format("%.0f", savingsRate) + "%");
            } else {
                tips.add("🌟 Excellent! You're saving "
                        + String.format("%.0f", savingsRate) + "% of your income!");
            }
        }

        if (totalExpense > 0) {
            tips.add("📊 Total expenses this month: ₹"
                    + String.format("%.0f", totalExpense));
        }

        return tips;
    }
}