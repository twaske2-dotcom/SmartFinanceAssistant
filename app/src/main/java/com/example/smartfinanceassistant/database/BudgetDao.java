package com.example.smartfinanceassistant.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfinanceassistant.models.Budget;

import java.util.List;

@Dao
public interface BudgetDao {
   // Abstraction
    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("SELECT * FROM budgets")
    LiveData<List<Budget>> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE category = :category")
    Budget getBudgetByCategory(String category);

    @Query("UPDATE budgets SET spent = spent + :amount WHERE category = :category")
    void updateSpent(String category, double amount);

    @Query("SELECT * FROM budgets WHERE spent >= monthlyLimit * 0.8")
    List<Budget> getOverBudgetCategories();
}