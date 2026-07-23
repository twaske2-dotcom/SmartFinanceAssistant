package com.example.smartfinanceassistant.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.smartfinanceassistant.database.AppDatabase;
import com.example.smartfinanceassistant.database.BudgetDao;
import com.example.smartfinanceassistant.database.ExpenseDao;
import com.example.smartfinanceassistant.models.Budget;
import com.example.smartfinanceassistant.models.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {

    private ExpenseDao expenseDao;
    private BudgetDao budgetDao;
    private LiveData<List<Expense>> allExpenses;
    private LiveData<List<Budget>> allBudgets;
    private ExecutorService executor;

    public ExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        expenseDao = db.expenseDao();
        budgetDao = db.budgetDao();
        allExpenses = expenseDao.getAllExpenses();
        allBudgets = budgetDao.getAllBudgets();
        executor = Executors.newFixedThreadPool(2);
    }

    // Expense operations
    public void insertExpense(Expense expense) {
        executor.execute(() -> expenseDao.insert(expense));
    }

    public void updateExpense(Expense expense) {
        executor.execute(() -> expenseDao.update(expense));
    }

    public void deleteExpense(Expense expense) {
        executor.execute(() -> expenseDao.delete(expense));
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    // Budget operations
    public void insertBudget(Budget budget) {
        executor.execute(() -> budgetDao.insert(budget));
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> budgetDao.update(budget));
    }

    public void deleteBudget(Budget budget) {
        executor.execute(() -> budgetDao.delete(budget));
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return allBudgets;
    }

    public void updateSpent(String category, double amount) {
        executor.execute(() -> budgetDao.updateSpent(category, amount));
    }

    public List<Budget> getOverBudgetCategories() {
        return budgetDao.getOverBudgetCategories();
    }
}