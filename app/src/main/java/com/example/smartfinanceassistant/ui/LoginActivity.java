package com.example.smartfinanceassistant.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartfinanceassistant.MainActivity;
import com.example.smartfinanceassistant.R;
import com.example.smartfinanceassistant.utils.BiometricHelper;
import com.example.smartfinanceassistant.utils.SecurityManager;
import com.example.smartfinanceassistant.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
  // Inheritance
public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin, btnBiometric;
    private TextView tvRegister, tvForgotPassword;
    private FirebaseAuth firebaseAuth;
    private SecurityManager securityManager;
    private SessionManager sessionManager;
    private BiometricHelper biometricHelper;
    // pollymorphism
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize
        firebaseAuth    = FirebaseAuth.getInstance();
        securityManager = SecurityManager.getInstance(this);
        sessionManager  = SessionManager.getInstance(this);

        // Check already logged in
        if (sessionManager.isSessionValid()) {
            goToMain();
            return;
        }

        // Initialize views
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        btnLogin         = findViewById(R.id.btnLogin);
        btnBiometric     = findViewById(R.id.btnBiometric);
        tvRegister       = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Setup Biometric
        setupBiometric();

        // Login button
        btnLogin.setOnClickListener(v -> loginUser());

        // Register
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this,
                    RegisterActivity.class));
        });

        // Forgot Password
        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText()
                    .toString().trim();
            if (email.isEmpty()) {
                etEmail.setError("Enter email first!");
                return;
            }
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    "Reset email sent!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void loginUser() {
        String email    = etEmail.getText()
                .toString().trim();
        String password = etPassword.getText()
                .toString().trim();

        // Validation
        if (!SecurityManager.isValidEmail(email)) {
            etEmail.setError("Invalid email!");
            return;
        }
        if (!SecurityManager.isValidPassword(password)) {
            etPassword.setError(
                    "Password min 6 characters!");
            return;
        }

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        // Firebase Login
        firebaseAuth.signInWithEmailAndPassword(
                        email, password)
                .addOnCompleteListener(task -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("SIGN IN");

                    if (task.isSuccessful()) {
                        // Save session
                        sessionManager.createSession(email);
                        securityManager.saveUserId(
                                firebaseAuth.getCurrentUser()
                                        .getUid());

                        Toast.makeText(this,
                                "Welcome back! 🎉",
                                Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Toast.makeText(this,
                                "Login failed: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setupBiometric() {
        if (BiometricHelper.isBiometricAvailable(this)) {
            btnBiometric.setVisibility(View.VISIBLE);

            biometricHelper = new BiometricHelper(this,
                    new BiometricHelper.BiometricCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(LoginActivity.this,
                                    "Authenticated! 🔓",
                                    Toast.LENGTH_SHORT).show();
                            goToMain();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(LoginActivity.this,
                                    error, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(LoginActivity.this,
                                    error, Toast.LENGTH_SHORT).show();
                        }
                    });

            btnBiometric.setOnClickListener(v ->
                    biometricHelper.authenticate());
        } else {
            btnBiometric.setVisibility(View.GONE);
        }
    }

    private void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}