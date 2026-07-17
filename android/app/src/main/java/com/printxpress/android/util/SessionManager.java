package com.printxpress.android.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.printxpress.android.PrintXpressApp;
import com.printxpress.android.data.model.AuthUser;

public class SessionManager {

    private static final String PREFS_NAME = "printxpress_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_EXPIRES_AT = "expires_at";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";

    private static SessionManager instance;

    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager(PrintXpressApp.getInstance());
        }
        return instance;
    }

    public void saveSession(String accessToken, String refreshToken, long expiresInSeconds, String userId, String email) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_REFRESH_TOKEN, refreshToken)
                .putLong(KEY_EXPIRES_AT, System.currentTimeMillis() + (expiresInSeconds * 1000))
                .putString(KEY_USER_ID, userId)
                .putString(KEY_EMAIL, email)
                .apply();
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    public boolean isAccessTokenExpired() {
        return System.currentTimeMillis() >= prefs.getLong(KEY_EXPIRES_AT, 0);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public AuthUser getCurrentUser() {
        if (!isLoggedIn()) return null;
        return new AuthUser(getUserId(), getEmail());
    }
}
