package com.example.smartfinanceassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityManager {

    private static final String PREFS_NAME =
            "secure_prefs";
    private static final String KEY_TOKEN =
            "jwt_token";
    private static final String KEY_USER_ID =
            "user_id";
    private static final String AES_KEY =
            "SmartFinance2024SecureKey123456!";

    private SharedPreferences securePrefs;
    private static SecurityManager instance;

    // Singleton Pattern
    public static SecurityManager getInstance(Context context) {
        if (instance == null) {
            instance = new SecurityManager(context);
        }
        return instance;
    }

    private SecurityManager(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to normal prefs
            securePrefs = context.getSharedPreferences(
                    PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    // ========== JWT Token Management ==========

    public void saveToken(String token) {
        securePrefs.edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public String getToken() {
        return securePrefs.getString(KEY_TOKEN, null);
    }

    public void saveUserId(String userId) {
        securePrefs.edit()
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public String getUserId() {
        return securePrefs.getString(KEY_USER_ID, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public void logout() {
        securePrefs.edit().clear().apply();
    }

    // ========== Password Hashing (SHA-256) ==========

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest
                    .getInstance("SHA-256");
            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    // ========== AES Encryption ==========

    public static String encrypt(String data) {
        try {
            byte[] keyBytes = AES_KEY.substring(0, 32)
                    .getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey =
                    new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(
                    data.getBytes(StandardCharsets.UTF_8));

            // Combine IV + encrypted data
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined,
                    iv.length, encrypted.length);

            return Base64.encodeToString(combined, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return data;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            byte[] combined = Base64.decode(
                    encryptedData, Base64.DEFAULT);

            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted,
                    0, encrypted.length);

            byte[] keyBytes = AES_KEY.substring(0, 32)
                    .getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey =
                    new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    new IvParameterSpec(iv));

            return new String(cipher.doFinal(encrypted),
                    StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return encryptedData;
        }
    }

    // ========== Input Validation ==========

    public static boolean isValidEmail(String email) {
        return email != null &&
                android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidAmount(String amount) {
        try {
            double val = Double.parseDouble(amount);
            return val > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}