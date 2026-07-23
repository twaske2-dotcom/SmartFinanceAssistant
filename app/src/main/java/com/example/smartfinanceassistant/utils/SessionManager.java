package com.example.smartfinanceassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

public class SessionManager {

    private static final String PREFS_NAME = "session_prefs";
    private static final String KEY_LAST_ACTIVE = "last_active";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "email";

    // Auto logout after 15 minutes of inactivity
    private static final long SESSION_TIMEOUT = 15 * 60 * 1000;

    private SharedPreferences prefs;
    private Handler handler;
    private Runnable sessionTimeoutRunnable;
    private SessionListener listener;
    private static SessionManager instance;

    public interface SessionListener {
        void onSessionExpired();
    }

    // Singleton
    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    private SessionManager(Context context) {
        prefs = context.getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);
        handler = new Handler(Looper.getMainLooper());
    }

    public void setSessionListener(SessionListener listener) {
        this.listener = listener;
    }

    // ========== Login/Logout ==========

    public void createSession(String email) {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_EMAIL, email)
                .putLong(KEY_LAST_ACTIVE,
                        System.currentTimeMillis())
                .apply();
        startSessionTimer();
    }

    public void logout() {
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .putString(KEY_EMAIL, null)
                .apply();
        stopSessionTimer();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    // ========== Session Timeout ==========

    public void updateLastActive() {
        prefs.edit()
                .putLong(KEY_LAST_ACTIVE,
                        System.currentTimeMillis())
                .apply();
        resetSessionTimer();
    }

    private void startSessionTimer() {
        sessionTimeoutRunnable = () -> {
            long lastActive = prefs.getLong(
                    KEY_LAST_ACTIVE, 0);
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastActive >= SESSION_TIMEOUT) {
                logout();
                if (listener != null) {
                    listener.onSessionExpired();
                }
            }
        };
        handler.postDelayed(
                sessionTimeoutRunnable, SESSION_TIMEOUT);
    }

    private void resetSessionTimer() {
        stopSessionTimer();
        startSessionTimer();
    }

    private void stopSessionTimer() {
        if (sessionTimeoutRunnable != null) {
            handler.removeCallbacks(sessionTimeoutRunnable);
        }
    }

    // ========== Session Check ==========

    public boolean isSessionValid() {
        if (!isLoggedIn()) return false;

        long lastActive = prefs.getLong(KEY_LAST_ACTIVE, 0);
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastActive >= SESSION_TIMEOUT) {
            logout();
            return false;
        }
        return true;
    }

    public long getRemainingTime() {
        long lastActive = prefs.getLong(KEY_LAST_ACTIVE, 0);
        long elapsed = System.currentTimeMillis() - lastActive;
        return Math.max(0, SESSION_TIMEOUT - elapsed);
    }
}