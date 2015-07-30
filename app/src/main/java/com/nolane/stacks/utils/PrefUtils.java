package com.nolane.stacks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;

public class PrefUtils {
    private static final String NAME = "preferences";

    @SuppressWarnings("NullableProblems")
    @NonNull
    private static Context context;

    public static void init(@NonNull Context context) {
        PrefUtils.context = context;
    }

    @NonNull
    public static SharedPreferences getPreferences() {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static final String KEY_MAX_PROGRESS = "max_progress";
    public static int getMaxProgress() {
        return getPreferences()
                .getInt(KEY_MAX_PROGRESS,
                        context.getResources().getInteger(R.integer.default_max_progress));
    }
    public static void setMaxProgress(int maxProgress) {
        getPreferences()
                .edit()
                .putInt(KEY_MAX_PROGRESS, maxProgress)
                .apply();
    }

    public static final String KEY_SESSION_PERIOD = "session_period";
    public static final int DEFAULT_SESSION_PERIOD = 18;
    /**
     * Key session period is amount of hours between two sessions of first box.
     * Periods of showing other boxes are multiplies of this one.
     */
    public static void setSessionPeriod(int value) {
        getPreferences()
                .edit()
                .putInt(KEY_SESSION_PERIOD, value)
                .apply();
    }
    /**
     * Key session period is amount of hours between two sessions of first box.
     * Periods of showing other boxes are multiplies of this one.
     */
    public static int getSessionPeriod() {
        return getPreferences().getInt(KEY_SESSION_PERIOD, DEFAULT_SESSION_PERIOD);
    }
}
