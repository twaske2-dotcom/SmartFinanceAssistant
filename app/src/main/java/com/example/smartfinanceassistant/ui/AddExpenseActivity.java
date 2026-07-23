package com.example.smartfinanceassistant.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartfinanceassistant.R;
import com.example.smartfinanceassistant.models.Expense;
import com.example.smartfinanceassistant.repository.ExpenseRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {
     // Inheritance
    private TextInputEditText etAmount, etTitle, etNote;
    private Spinner spinnerCategory;
    private Button btnSave;
    private TextView tvTitle;
    private ExpenseRepository repository;
    private String type = "expense";

    private String[] categories = {
            "Food", "Travel", "Shopping",
            "Bills", "Entertainment",
            "Healthcare", "Education", "Other"
    };

    @Override
    // Pollymorphism
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Get type from intent
        type = getIntent().getStringExtra("type");
        if (type == null) type = "expense";

        // Initialize views
        etAmount        = findViewById(R.id.etAmount);
        etTitle         = findViewById(R.id.etTitle);
        etNote          = findViewById(R.id.etNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave         = findViewById(R.id.btnSave);
        tvTitle         = findViewById(R.id.tvTitle);

        // Set title based on type
        if (type.equals("income")) {
            tvTitle.setText("Add Income");
            btnSave.setBackgroundTintList(
                    getColorStateList(android.R.color.holo_green_dark));
        } else {
            tvTitle.setText("Add Expense");
        }

        // Setup category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Initialize repository
        repository = new ExpenseRepository(getApplication());

        // Save button
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String title     = etTitle.getText().toString().trim();
        String note      = etNote.getText().toString().trim();
        String category  = spinnerCategory.getSelectedItem().toString();

        // Validation
        if (amountStr.isEmpty()) {
            etAmount.setError("Please enter amount!");
            return;
        }
        if (title.isEmpty()) {
            etTitle.setError("Please enter title!");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String date = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        // Create and save expense
        Expense expense = new Expense(
                title, category, amount, date, note, type);
        repository.insertExpense(expense);

        Toast.makeText(this,
                type.equals("income") ?
                        "Income added!" : "Expense added!",
                Toast.LENGTH_SHORT).show();

        finish(); // Go back to MainActivity
    }
}