package com.nolane.stacks.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.DeleteReceiver;

public class PreferencesUtils {
    private static final String NAME = "preferences";

    @NonNull
    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static final String KEY_MAX_PROGRESS = "max_progress";
    public static long getMaxProgress(@NonNull Context context) {
        return getPreferences(context)
                .getLong(KEY_MAX_PROGRESS,
                        context.getResources().getInteger(R.integer.default_max_progress));
    }
    public static void setMaxProgress(@NonNull Context context, long maxProgress) {
        getPreferences(context)
                .edit()
                .putLong(KEY_MAX_PROGRESS, maxProgress)
                .commit();
    }

    private static final String KEY_MIN_PROGRESS = "min_progress";
    public static long getMinProgress(@NonNull Context context) {
        return getPreferences(context)
                .getLong(KEY_MIN_PROGRESS,
                        context.getResources().getInteger(R.integer.default_min_progress));
    }
    public static void setMinProgress(@NonNull Context context, long minProgress) {
        getPreferences(context)
                .edit()
                .putLong(KEY_MIN_PROGRESS, minProgress)
                .commit();
    }

    private static final String KEY_DELETE_SCHEDULED = "delete_scheduled";
    public static void notifyDeleted(@NonNull Context context) {
        context = context.getApplicationContext();
        boolean deleteScheduled = getPreferences(context)
                .getBoolean(KEY_DELETE_SCHEDULED, false);
        if (!deleteScheduled) {
            getPreferences(context)
                    .edit()
                    .putBoolean(KEY_DELETE_SCHEDULED, true)
                    .commit();
            Intent intent = new Intent(context, DeleteReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long oneMinute = 1000 * 60;
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + oneMinute, pendingIntent);
        }
    }
    public static void deletionDone(@NonNull Context context) {
        getPreferences(context)
                .edit()
                .putBoolean(KEY_DELETE_SCHEDULED, false)
                .commit();
    }
}
