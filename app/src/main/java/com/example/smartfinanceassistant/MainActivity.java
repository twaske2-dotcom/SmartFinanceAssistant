package com.example.smartfinanceassistant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinanceassistant.models.Expense;
import com.example.smartfinanceassistant.models.Budget;
import com.example.smartfinanceassistant.repository.ExpenseRepository;
import com.example.smartfinanceassistant.ui.AddExpenseActivity;
import com.example.smartfinanceassistant.ui.BudgetActivity;
import com.example.smartfinanceassistant.ui.EMICalculatorActivity;
import com.example.smartfinanceassistant.ui.ProductSearchActivity;
import com.example.smartfinanceassistant.ui.TransactionAdapter;
import com.example.smartfinanceassistant.utils.InsightsEngine;
import com.example.smartfinanceassistant.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SessionManager.SessionListener {

    private TextView tvTotalBalance, tvTotalIncome,
            tvTotalExpense, tvInsights;
    private Button btnAddExpense, btnAddIncome,
            btnSearch, btnBudget, btnEMI;
    private RecyclerView rvTransactions;
    private ExpenseRepository repository;
    private TransactionAdapter adapter;
    private SessionManager sessionManager;
    private List<Expense> expenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session check
        sessionManager = SessionManager.getInstance(this);
        sessionManager.setSessionListener(this);

        // Initialize views
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome  = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvInsights     = findViewById(R.id.tvInsights);
        btnAddExpense  = findViewById(R.id.btnAddExpense);
        btnAddIncome   = findViewById(R.id.btnAddIncome);
        btnSearch      = findViewById(R.id.btnSearch);
        btnBudget      = findViewById(R.id.btnBudget);
        btnEMI         = findViewById(R.id.btnEMI);
        rvTransactions = findViewById(R.id.rvTransactions);

        // Setup RecyclerView
        adapter = new TransactionAdapter(new ArrayList<>());
        rvTransactions.setLayoutManager(
                new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        // Initialize repository
        repository = new ExpenseRepository(getApplication());

        // Observe expenses
        repository.getAllExpenses().observe(this, expenses -> {
            expenseList = expenses;
            adapter.updateList(expenses);
            updateDashboard(expenses);
            updateInsights(expenses);
        });

        // Add Expense
        btnAddExpense.setOnClickListener(v -> {
            sessionManager.updateLastActive();
            Intent intent = new Intent(this,
                    AddExpenseActivity.class);
            intent.putExtra("type", "expense");
            startActivity(intent);
        });

        // Add Income
        btnAddIncome.setOnClickListener(v -> {
            sessionManager.updateLastActive();
            Intent intent = new Intent(this,
                    AddExpenseActivity.class);
            intent.putExtra("type", "income");
            startActivity(intent);
        });

        // Product Search
        btnSearch.setOnClickListener(v -> {
            sessionManager.updateLastActive();
            startActivity(new Intent(this,
                    ProductSearchActivity.class));
        });

        // Budget
        btnBudget.setOnClickListener(v -> {
            sessionManager.updateLastActive();
            startActivity(new Intent(this,
                    BudgetActivity.class));
        });

        // ✅ EMI Calculator — FIXED!
        btnEMI.setOnClickListener(v -> {
            sessionManager.updateLastActive();
            startActivity(new Intent(this,
                    EMICalculatorActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.updateLastActive();
    }

    @Override
    public void onSessionExpired() {
        startActivity(new Intent(this,
                com.example.smartfinanceassistant.ui
                        .LoginActivity.class));
        finish();
    }

    private void updateDashboard(List<Expense> expenses) {
        double totalIncome = 0, totalExpense = 0;

        for (Expense e : expenses) {
            if (e.type.equals("income"))
                totalIncome += e.amount;
            else
                totalExpense += e.amount;
        }

        double balance = totalIncome - totalExpense;

        tvTotalBalance.setText("Balance: ₹" +
                String.format("%.2f", balance));
        tvTotalIncome.setText("₹" +
                String.format("%.2f", totalIncome));
        tvTotalExpense.setText("₹" +
                String.format("%.2f", totalExpense));
    }

    private void updateInsights(List<Expense> expenses) {
        InsightsEngine engine = new InsightsEngine(
                expenses, new ArrayList<>());
        List<String> insights = engine.generateInsights();

        StringBuilder sb = new StringBuilder();
        for (String insight : insights) {
            sb.append(insight).append("\n\n");
        }
        tvInsights.setText(sb.toString().trim());
    }
}