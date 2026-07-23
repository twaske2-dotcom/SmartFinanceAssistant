package com.example.smartfinanceassistant.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartfinanceassistant.R;
import com.example.smartfinanceassistant.models.Budget;
import com.example.smartfinanceassistant.repository.ExpenseRepository;

import java.util.ArrayList;

public class BudgetActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etLimit;
    private Button btnSetBudget;
    private RecyclerView rvBudgets;
    private ExpenseRepository repository;

    private String[] categories = {
            "Food", "Travel", "Shopping",
            "Bills", "Entertainment",
            "Healthcare", "Education", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        etLimit         = findViewById(R.id.etLimit);
        btnSetBudget    = findViewById(R.id.btnSetBudget);
        rvBudgets       = findViewById(R.id.rvBudgets);

        // Setup spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Setup RecyclerView
        rvBudgets.setLayoutManager(
                new LinearLayoutManager(this));

        repository = new ExpenseRepository(getApplication());

        // Observe budgets
        repository.getAllBudgets().observe(this, budgets -> {
            // Update list
        });

        btnSetBudget.setOnClickListener(v -> {
            String category = spinnerCategory
                    .getSelectedItem().toString();
            String limitStr = etLimit.getText()
                    .toString().trim();

            if (limitStr.isEmpty()) {
                etLimit.setError("Enter budget limit!");
                return;
            }

            double limit = Double.parseDouble(limitStr);
            String month = java.time.LocalDate.now()
                    .toString().substring(0, 7);

            Budget budget = new Budget(
                    category, limit, 0, month);
            repository.insertBudget(budget);

            Toast.makeText(this,
                    category + " budget set: ₹" + limit,
                    Toast.LENGTH_SHORT).show();

            etLimit.setText("");
        });
    }
}