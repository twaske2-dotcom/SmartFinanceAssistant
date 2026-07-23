package com.example.smartfinanceassistant.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartfinanceassistant.models.Expense;

import java.util.List;

@Dao
public interface ExpenseDao {
    // Abstraction
    @Insert
    void insert(Expense expense);

    @Update
    void update(Expense expense);

    @Delete
    void delete(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE type = 'expense'")
    LiveData<List<Expense>> getAllExpensesOnly();

    @Query("SELECT * FROM expenses WHERE type = 'income'")
    LiveData<List<Expense>> getAllIncome();

    @Query("SELECT SUM(amount) FROM expenses WHERE type = 'expense'")
    double getTotalExpenses();

    @Query("SELECT SUM(amount) FROM expenses WHERE type = 'income'")
    double getTotalIncome();

    @Query("SELECT * FROM expenses WHERE category = :category")
    LiveData<List<Expense>> getExpensesByCategory(String category);

    @Query("SELECT SUM(amount) FROM expenses WHERE category = :category AND type = 'expense'")
    double getTotalByCategory(String category);
}