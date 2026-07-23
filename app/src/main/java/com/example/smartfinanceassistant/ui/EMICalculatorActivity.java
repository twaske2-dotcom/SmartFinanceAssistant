package com.example.smartfinanceassistant.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smartfinanceassistant.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;

public class EMICalculatorActivity extends AppCompatActivity {

    TextInputEditText etLoanAmount, etInterestRate, etTenure;
    Button btnCalculate;
    TextView tvEMI, tvTotalAmount, tvTotalInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emicalculator_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views link karo
        etLoanAmount    = findViewById(R.id.etLoanAmount);
        etInterestRate  = findViewById(R.id.etInterestRate);
        etTenure        = findViewById(R.id.etTenure);
        btnCalculate    = findViewById(R.id.btnCalculate);
        tvEMI           = findViewById(R.id.tvEMI);
        tvTotalAmount   = findViewById(R.id.tvTotalAmount);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);

        // Button click
        btnCalculate.setOnClickListener(v -> calculateEMI());
    }

    private void calculateEMI() {
        String loanStr     = etLoanAmount.getText().toString().trim();
        String rateStr     = etInterestRate.getText().toString().trim();
        String tenureStr   = etTenure.getText().toString().trim();

        // Validation
        if (loanStr.isEmpty() || rateStr.isEmpty() || tenureStr.isEmpty()) {
            Toast.makeText(this, "Bhai! Sabhi fields bharo", Toast.LENGTH_SHORT).show();
            return;
        }

        double principal = Double.parseDouble(loanStr);
        double annualRate = Double.parseDouble(rateStr);
        int tenure = Integer.parseInt(tenureStr);

        // EMI Formula: P * r * (1+r)^n / ((1+r)^n - 1)
        double monthlyRate = annualRate / (12 * 100);  // % to monthly
        double emi;

        if (monthlyRate == 0) {
            // Agar interest 0% ho
            emi = principal / tenure;
        } else {
            double power = Math.pow(1 + monthlyRate, tenure);
            emi = (principal * monthlyRate * power) / (power - 1);
        }

        double totalAmount   = emi * tenure;
        double totalInterest = totalAmount - principal;

        // Format karo
        DecimalFormat df = new DecimalFormat("##,##,##0.00");

        tvEMI.setText("₹" + df.format(emi));
        tvTotalAmount.setText("₹" + df.format(totalAmount));
        tvTotalInterest.setText("₹" + df.format(totalInterest));
    }
}