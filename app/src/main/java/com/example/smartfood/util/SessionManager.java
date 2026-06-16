package com.example.smartfood.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "smart_food_session";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLoginUser(long userId) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public long getCurrentUserId() {
        return preferences.getLong(KEY_USER_ID, -1);
    }

    public boolean isLoggedIn() {
        return getCurrentUserId() > 0;
    }

    public void logout() {
        preferences.edit().remove(KEY_USER_ID).apply();
    }
}
