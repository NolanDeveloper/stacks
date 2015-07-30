package com.nolane.stacks.provider;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nolane.stacks.utils.GeneralUtils;

import java.io.Serializable;

public class Card implements Serializable {
    public static class CardsFactory implements CursorWrapper.ModelFactory<Card> {
        private int id;
        private int front;
        private int back;
        private int progress;
        private int nextShowing;
        private int stackId;

        @Override
        public void prepare(@NonNull Cursor c) {
            id = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_ID);
            if (-1 == id) {
                throw new IllegalArgumentException("Each query must request id column.");
            }
            front = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_FRONT);
            back = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_BACK);
            progress = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_PROGRESS);
            nextShowing = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_NEXT_SHOWING);
            stackId = c.getColumnIndex(CardsDatabase.CardsColumns.CARD_STACK_ID);
        }

        @Override
        @NonNull
        public Card wrapRow(@NonNull Cursor c) {
            return new Card(
                    c.getLong(id),
                    -1 != front ? c.getString(front) : null,
                    -1 != back ? c.getString(back) : null,
                    -1 != progress ? c.getInt(progress) : null,
                    -1 != nextShowing ? c.getLong(nextShowing) : null,
                    -1 != stackId ? c.getLong(stackId) : null);
        }
    }

    public static final int MAX_FRONT_LEN = 40;
    public static boolean checkFront(@NonNull CharSequence front) {
        return front.length() < MAX_FRONT_LEN;
    }

    public static final int MAX_BACK_LEN = 40;
    public static boolean checkBack(@NonNull CharSequence back) {
        return back.length() < MAX_BACK_LEN;
    }

    @NonNull
    public final Long id;
    @Nullable
    public final String front;
    @Nullable
    public final String back;
    @Nullable
    public final Integer progress;
    @Nullable
    public final Long nextShowing;
    @Nullable
    public final Long stackId;

    public Card(@NonNull Long id, @Nullable String front, @Nullable String back,
                @Nullable Integer progress, @Nullable Long nextShowing, @Nullable Long stackId) {
        if ((null != front) && !checkFront(front)) {
            throw new IllegalArgumentException("Front text is too long.");
        }
        if ((null != back) && !checkBack(back)) {
            throw new IllegalArgumentException("Back text is too long.");
        }
        this.id = id;
        this.front = front;
        this.back = back;
        this.progress = progress;
        this.nextShowing = nextShowing;
        this.stackId = stackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return GeneralUtils.equals(id, card.id) &&
                GeneralUtils.equals(front, card.front) &&
                GeneralUtils.equals(back, card.back) &&
                GeneralUtils.equals(progress, card.progress) &&
                GeneralUtils.equals(nextShowing, card.nextShowing) &&
                GeneralUtils.equals(stackId, card.stackId);
    }

    @Override
    public int hashCode() {
        return GeneralUtils.hash(id, front, back, progress, nextShowing, stackId);
    }
}
