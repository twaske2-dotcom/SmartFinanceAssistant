package com.example.smartfinanceassistant.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String category;
    public double monthlyLimit;
    public double spent;
    public String month; // "2026-05"

    // Constructor
    public Budget(String category, double monthlyLimit,
                  double spent, String month) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.spent = spent;
        this.month = month;
    }

    // Getters
    public int getId() { return id; }
    public String getCategory() { return category; }
    public double getMonthlyLimit() { return monthlyLimit; }
    public double getSpent() { return spent; }
    public String getMonth() { return month; }

    // Helper method
    public double getRemainingBudget() {
        return monthlyLimit - spent;
    }

    public boolean isOverBudget() {
        return spent > monthlyLimit;
    }

    public int getPercentageUsed() {
        if (monthlyLimit == 0) return 0;
        return (int) ((spent / monthlyLimit) * 100);
    }
}