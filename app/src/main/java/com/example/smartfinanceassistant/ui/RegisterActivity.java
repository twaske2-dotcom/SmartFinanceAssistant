package com.example.smartfinanceassistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartfinanceassistant.R;
import com.example.smartfinanceassistant.utils.SecurityManager;
import com.example.smartfinanceassistant.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail,
            etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth   = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(this);

        // Initialize views
        etName            = findViewById(R.id.etName);
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        tvLogin           = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> registerUser());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this,
                    LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name     = etName.getText()
                .toString().trim();
        String email    = etEmail.getText()
                .toString().trim();
        String password = etPassword.getText()
                .toString().trim();
        String confirm  = etConfirmPassword.getText()
                .toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Enter your name!");
            return;
        }
        if (!SecurityManager.isValidEmail(email)) {
            etEmail.setError("Invalid email!");
            return;
        }
        if (!SecurityManager.isValidPassword(password)) {
            etPassword.setError("Min 6 characters!");
            return;
        }
        if (!password.equals(confirm)) {
            etConfirmPassword.setError(
                    "Passwords don't match!");
            return;
        }

        // Show loading
        btnRegister.setEnabled(false);
        btnRegister.setText("Creating account...");

        // Firebase Register
        firebaseAuth.createUserWithEmailAndPassword(
                        email, password)
                .addOnCompleteListener(task -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("CREATE ACCOUNT");

                    if (task.isSuccessful()) {
                        sessionManager.createSession(email);
                        Toast.makeText(this,
                                "Account created! Welcome 🎉",
                                Toast.LENGTH_SHORT).show();

                        // Go to Login
                        startActivity(new Intent(this,
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException()
                                        .getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}