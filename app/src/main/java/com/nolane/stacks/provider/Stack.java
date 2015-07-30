package com.nolane.stacks.provider;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nolane.stacks.utils.GeneralUtils;

import java.io.Serializable;

import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_COLOR;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_COUNT_CARDS;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_COUNT_IN_LEARNING;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_ID;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_LANGUAGE;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_MAX_IN_LEARNING;
import static com.nolane.stacks.provider.CardsDatabase.StacksColumns.STACK_TITLE;

public class Stack implements Serializable {
    public static class StackFactory implements CursorWrapper.ModelFactory<Stack> {
        private int id;
        private int title;
        private int maxInLearning;
        private int countCards;
        private int countInLearning;
        private int language;
        private int color;

        @Override
        public void prepare(@NonNull Cursor c) {
            id = c.getColumnIndex(STACK_ID);
            if (-1 == id) {
                throw new IllegalArgumentException("Each query must request id column.");
            }
            title = c.getColumnIndex(STACK_TITLE);
            maxInLearning = c.getColumnIndex(STACK_MAX_IN_LEARNING);
            countCards = c.getColumnIndex(STACK_COUNT_CARDS);
            countInLearning = c.getColumnIndex(STACK_COUNT_IN_LEARNING);
            language = c.getColumnIndex(STACK_LANGUAGE);
            color = c.getColumnIndex(STACK_COLOR);
        }

        @Override
        @NonNull
        public Stack wrapRow(@NonNull Cursor c) {
            return new Stack(
                    c.getLong(id),
                    -1 != title ? c.getString(title) : null,
                    -1 != maxInLearning ? c.getInt(maxInLearning) : null,
                    -1 != countCards ? c.getInt(countCards) : null,
                    -1 != countInLearning ? c.getInt(countInLearning) : null,
                    -1 != language ? c.getString(language) : null,
                    -1 != color ? c.getInt(color) : null
            );
        }
    }

    private static final int MAX_TITLE_LEN = 30;
    public static boolean checkTitle(@NonNull CharSequence title) {
        return title.length() <= MAX_TITLE_LEN;
    }

    private static final int MAX_LANGUAGE_LEN = 20;
    public static boolean checkLanguage(@NonNull CharSequence language) {
        return language.length() < MAX_LANGUAGE_LEN;
    }

    @NonNull
    public final Long id;
    @Nullable
    public final String title;
    @Nullable
    public final Integer maxInLearning;
    @Nullable
    public final Integer countCards;
    @Nullable
    public final Integer countInLearning;
    @Nullable
    public final String language;
    @Nullable
    public final Integer color;

    public Stack(@NonNull Long id, @Nullable String title, @Nullable Integer maxInLearning,
                 @Nullable Integer countCards, @Nullable Integer countInLearning,
                 @Nullable String language, @Nullable Integer color) {
        if ((null != title) && !checkTitle(title)) {
            throw new IllegalArgumentException("Title is too long.");
        }
        if ((null != language) && !checkLanguage(language)) {
            throw new IllegalArgumentException("Language is too long.");
        }
        this.id = id;
        this.title = title;
        this.maxInLearning = maxInLearning;
        this.countCards = countCards;
        this.countInLearning = countInLearning;
        this.language = language;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stack stack = (Stack) o;
        return GeneralUtils.equals(id, stack.id) &&
                GeneralUtils.equals(title, stack.title) &&
                GeneralUtils.equals(maxInLearning, stack.maxInLearning) &&
                GeneralUtils.equals(countCards, stack.countCards) &&
                GeneralUtils.equals(countInLearning, stack.countInLearning) &&
                GeneralUtils.equals(language, stack.language) &&
                GeneralUtils.equals(color, stack.color);
    }

    @Override
    public int hashCode() {
        return GeneralUtils.hash(id, title, maxInLearning, countCards, countInLearning, language, color);
    }
}
