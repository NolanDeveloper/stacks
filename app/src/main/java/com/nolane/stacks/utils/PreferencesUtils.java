package com.nolane.stacks.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;

import java.util.Calendar;

public class PreferencesUtils {
    private static final String NAME = "preferences";

    @NonNull
    private static SharedPreferences getPreferences(@NonNull Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    private static final String KEY_MAX_PROGRESS = "max_progress";
    public static int getMaxProgress(@NonNull Context context) {
        return getPreferences(context)
                .getInt(KEY_MAX_PROGRESS,
                        context.getResources().getInteger(R.integer.default_max_progress));
    }
    public static void setMaxProgress(@NonNull Context context, int maxProgress) {
        getPreferences(context)
                .edit()
                .putInt(KEY_MAX_PROGRESS, maxProgress)
                .apply();
    }

    private static final String KEY_MIN_PROGRESS = "min_progress";
    public static int getMinProgress(@NonNull Context context) {
        return getPreferences(context)
                .getInt(KEY_MIN_PROGRESS,
                        context.getResources().getInteger(R.integer.default_min_progress));
    }
    public static void setMinProgress(@NonNull Context context, int minProgress) {
        getPreferences(context)
                .edit()
                .putInt(KEY_MIN_PROGRESS, minProgress)
                .apply();
    }

    private static final String KEY_LAST_ANSWER = "last_answer";
    private static final String KEY_STREAK = "streak";
    private static final String KEY_BEST_STREAK = "best_streak";
    public static void updateStreak(@NonNull Context context) {
    }
    public static int getStreak(@NonNull Context context) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar today = Calendar.getInstance();
        Calendar lastAnswer = Calendar.getInstance();
        lastAnswer.setTimeInMillis(getPreferences(context).getLong(KEY_LAST_ANSWER, 0));
        if (yesterday.get(Calendar.YEAR) == lastAnswer.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == lastAnswer.get(Calendar.DAY_OF_YEAR)
                || today.get(Calendar.YEAR) == lastAnswer.get(Calendar.YEAR)
                && today.get(Calendar.DAY_OF_YEAR) == lastAnswer.get(Calendar.DAY_OF_YEAR)) {
            return getPreferences(context).getInt(KEY_STREAK, 0);
        } else {
            getPreferences(context)
                    .edit()
                    .putInt(KEY_STREAK, 0)
                    .apply();
            return 0;
        }
    }
    public static int getBestStreak(@NonNull Context context) {
        return getPreferences(context).getInt(KEY_BEST_STREAK, 0);
    }

    private static final String KEY_TOTAL_CARDS = "total_cards";
    public static void cardWasAdded(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        int totalCards = preferences.getInt(KEY_TOTAL_CARDS, 0);
        preferences
                .edit()
                .putInt(KEY_TOTAL_CARDS, totalCards + 1)
                .apply();
    }
    public static void cardWasDeleted(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        int totalCards = preferences.getInt(KEY_TOTAL_CARDS, 0);
        preferences
                .edit()
                .putInt(KEY_TOTAL_CARDS, totalCards - 1)
                .apply();
    }
    public static int getTotalCards(@NonNull Context context) {
        return getPreferences(context).getInt(KEY_TOTAL_CARDS, 0);
    }

    private static final String KEY_TOTAL_PROGRESS = "total_progress";
    public static void progressUp(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        int totalProgress = preferences.getInt(KEY_TOTAL_PROGRESS, 0);
        preferences
                .edit()
                .putInt(KEY_TOTAL_PROGRESS, totalProgress + 1)
                .apply();
    }
    public static void progressDown(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        int totalProgress = preferences.getInt(KEY_TOTAL_PROGRESS, 0);
        preferences
                .edit()
                .putInt(KEY_TOTAL_PROGRESS, totalProgress - 1)
                .apply();
    }
    public static int getTotalProgress(@NonNull Context context) {
        return getPreferences(context).getInt(KEY_TOTAL_PROGRESS, 0);
    }

    private static final String KEY_CARDS_LEARNED = "cards_learned";
    public static void learnedCard(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        int cardsLearned = preferences.getInt(KEY_CARDS_LEARNED, 0);
        preferences
                .edit()
                .putInt(KEY_CARDS_LEARNED, cardsLearned + 1)
                .apply();
    }
    public static int getCardsLearned(@NonNull Context context) {
        return getPreferences(context).getInt(KEY_CARDS_LEARNED, 0);
    }

    public static final String KEY_TOTAL_ANSWERS = "total_answers";
    public static void newAnswer(@NonNull Context context) {
        SharedPreferences preferences = getPreferences(context);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        Calendar lastAnswer = Calendar.getInstance();
        lastAnswer.setTimeInMillis(preferences.getLong(KEY_LAST_ANSWER, 0));
        int totalAnswers = preferences.getInt(KEY_TOTAL_ANSWERS, 0);
        if (yesterday.get(Calendar.YEAR) == lastAnswer.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == lastAnswer.get(Calendar.DAY_OF_YEAR)) {
            int streak = preferences.getInt(KEY_STREAK, 0) + 1;
            int bestStreak = preferences.getInt(KEY_BEST_STREAK, 0);
            preferences
                    .edit()
                    .putInt(KEY_STREAK, streak)
                    .putInt(KEY_BEST_STREAK, Math.max(streak, bestStreak))
                    .putInt(KEY_TOTAL_ANSWERS, totalAnswers + 1)
                    .apply();
        } else if (today.get(Calendar.YEAR) != lastAnswer.get(Calendar.YEAR)
                || today.get(Calendar.DAY_OF_YEAR) != lastAnswer.get(Calendar.DAY_OF_YEAR)) {
            preferences
                    .edit()
                    .putInt(KEY_TOTAL_ANSWERS, totalAnswers + 1)
                    .apply();
        } else {
            preferences
                    .edit()
                    .putInt(KEY_TOTAL_ANSWERS, totalAnswers + 1)
                    .putInt(KEY_STREAK, 1)
                    .apply();
        }
    }
    public static int getTotalAnswers(@NonNull Context context) {
        return getPreferences(context).getInt(KEY_TOTAL_ANSWERS, 0);
    }
}
