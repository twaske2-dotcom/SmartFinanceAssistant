package com.example.smartfinanceassistant.utils;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class BiometricHelper {

    private AppCompatActivity activity;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricCallback callback;

    public interface BiometricCallback {
        void onSuccess();
        void onFailure(String error);
        void onError(String error);
    }

    public BiometricHelper(AppCompatActivity activity,
                           BiometricCallback callback) {
        this.activity = activity;
        this.callback = callback;
        setupBiometric();
    }

    private void setupBiometric() {
        Executor executor = ContextCompat.getMainExecutor(activity);

        biometricPrompt = new BiometricPrompt(activity,
                executor,
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(
                            BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        callback.onSuccess();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        callback.onFailure(
                                "Authentication failed! Try again.");
                    }

                    @Override
                    public void onAuthenticationError(
                            int errorCode, CharSequence errString) {
                        super.onAuthenticationError(
                                errorCode, errString);
                        callback.onError(errString.toString());
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Smart Finance Assistant")
                .setSubtitle("Verify your identity")
                .setDescription(
                        "Use fingerprint or face to access your finances")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
    }

    // Check if biometric is available
    public static boolean isBiometricAvailable(Context context) {
        BiometricManager biometricManager =
                BiometricManager.from(context);

        switch (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return true;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return false;
            default:
                return false;
        }
    }

    // Show biometric prompt
    public void authenticate() {
        biometricPrompt.authenticate(promptInfo);
    }
}