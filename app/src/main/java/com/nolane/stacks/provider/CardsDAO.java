package com.nolane.stacks.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nolane.stacks.provider.CardsDatabase.CardsColumns;
import com.nolane.stacks.provider.CardsDatabase.StacksColumns;
import com.nolane.stacks.provider.CardsDatabase.Tables;
import com.nolane.stacks.utils.PrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;

public class CardsDAO {
    private static CardsDAO INSTANCE;

    private final CardsDatabase db;
    private final Context context;

    private CardsDAO(@NonNull Context context) {
        this.db = new CardsDatabase(context);
        this.context = context.getApplicationContext();
    }

    public static CardsDAO getInstance() {
        return INSTANCE;
    }

    public static void init(@NonNull Context context) {
        INSTANCE = new CardsDAO(context);
    }

    private static <T> Observable<T> makeObservable(@NonNull final Callable<T> function) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(function.call());
                } catch (Throwable e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @NonNull
    public Observable<Stack> createStack(@NonNull final String title, @NonNull final String language,
                                         final int maxCardsInLearning, final int color) {
        return makeObservable(new Callable<Stack>() {
            @Override
            public Stack call() throws Exception {
                ContentValues values = new ContentValues();
                values.put(StacksColumns.STACK_TITLE, title);
                values.put(StacksColumns.STACK_LANGUAGE, language);
                values.put(StacksColumns.STACK_MAX_IN_LEARNING, maxCardsInLearning);
                values.put(StacksColumns.STACK_COLOR, color);
                long id = db.getWritableDatabase().insert(Tables.STACKS, null, values);
                return new Stack(id, title, maxCardsInLearning, null, null, language, color);
            }
        });
    }

    private Card addCard(long stackId, @NonNull String front, @NonNull String back,
                         boolean startLearning) {
        ContentValues values = new ContentValues();
        values.put(CardsColumns.CARD_FRONT, front);
        values.put(CardsColumns.CARD_BACK, back);
        values.put(CardsColumns.CARD_STACK_ID, stackId);
        if (startLearning) {
            values.put(CardsColumns.CARD_PROGRESS, 1);
            values.put(CardsColumns.CARD_NEXT_SHOWING, System.currentTimeMillis());
        }
        long id = db.getWritableDatabase().insertOrThrow(Tables.CARDS, null, values);

        String sql = "UPDATE " + Tables.STACKS + " SET ";
        sql += StacksColumns.STACK_COUNT_CARDS + " = " + StacksColumns.STACK_COUNT_CARDS + " + 1";
        if (startLearning) {
            sql += ", " + StacksColumns.STACK_COUNT_CARDS + " = " + StacksColumns.STACK_COUNT_CARDS + " + 1";
        }
        sql += " WHERE " + StacksColumns.STACK_ID + " = " + stackId;
        db.getWritableDatabase().execSQL(sql);

        return new Card(id, front, back, startLearning ? 1 : 0, null, stackId);
    }

    @NonNull
    public Observable<Card[]> addCard(final long stackId, final boolean bidirectional,
                                      @NonNull final String front, @NonNull final String back) {
        return makeObservable(new Callable<Card[]>() {
            @Override
            public Card[] call() throws Exception {
                Cursor query = db.getReadableDatabase().query(
                        Tables.STACKS,
                        new String[] {
                                StacksColumns.STACK_COUNT_IN_LEARNING,
                                StacksColumns.STACK_MAX_IN_LEARNING
                        }, StacksColumns.STACK_ID + " = " + stackId,
                        null, null, null, null);
                if (!query.moveToFirst()) {
                    throw new IllegalArgumentException("No stack with id = " + stackId);
                }
                int countInLearning = query.getInt(0);
                int maxInLearning= query.getInt(1);
                query.close();
                Card[] result = new Card[bidirectional ? 2 : 1];
                result[0] = addCard(stackId, front, back, countInLearning < maxInLearning);
                if (bidirectional) {
                    result[1] = addCard(stackId, back, front, countInLearning + 1 < maxInLearning);
                }
                return result;
            }
        });
    }

    @NonNull
    public Observable<Stack> changeStack(final long stackId, @Nullable final String title,
                                         @Nullable final String language,
                                         @Nullable final Integer color) {
        return makeObservable(new Callable<Stack>() {
            @Override
            public Stack call() throws Exception {
                ContentValues values = new ContentValues();
                values.put(StacksColumns.STACK_TITLE, title);
                values.put(StacksColumns.STACK_LANGUAGE, language);
                values.put(StacksColumns.STACK_COLOR, color);
                int affected = db.getWritableDatabase().update(
                        Tables.STACKS, values, StacksColumns.STACK_ID + " = " + stackId, null);
                if (0 == affected) {
                    throw new IllegalArgumentException("No stack with id = " + stackId);
                }
                return new Stack(stackId, title, null, null, null, language, color);
            }
        });
    }

    @NonNull
    public Observable<Card> changeCard(final long cardId, @Nullable final String front,
                                       @Nullable final String back) {
        return makeObservable(new Callable<Card>() {
            @Override
            public Card call() throws Exception {
                ContentValues values = new ContentValues();
                values.put(CardsColumns.CARD_FRONT, front);
                values.put(CardsColumns.CARD_BACK, back);
                int affected = db.getWritableDatabase().update(
                        Tables.CARDS, values, CardsColumns.CARD_ID + " = " + cardId, null);
                if (0 == affected) {
                    throw new IllegalArgumentException("No card with id = " + cardId);
                }
                return new Card(cardId, front, back, null, null, null);
            }
        });
    }

    @NonNull
    public Observable<Void> promoteCard(final long cardId) {
        return makeObservable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Cursor query = db.getReadableDatabase().query(
                        Tables.CARDS,
                        new String[]{
                                CardsColumns.CARD_STACK_ID,
                                CardsColumns.CARD_PROGRESS
                        }, CardsColumns.CARD_ID + " = " + cardId,
                        null, null, null, null);
                if (!query.moveToFirst()) {
                    throw new IllegalArgumentException("No card with id = " + cardId);
                }
                long stackId = query.getLong(0);
                int progress = query.getInt(1);
                query.close();
                int maxProgress = PrefUtils.getMaxProgress();
                ContentValues values = new ContentValues();
                values.put(CardsColumns.CARD_PROGRESS, progress + 1);
                values.put(CardsColumns.CARD_NEXT_SHOWING,
                        System.currentTimeMillis() +
                                (progress + 1) * PrefUtils.getSessionPeriod() * 3600000);
                int affected = db.getWritableDatabase().update(
                        Tables.CARDS, values, CardsColumns.CARD_ID + " = " + cardId, null);
                if (0 == affected) {
                    throw new IllegalArgumentException("No card with id = " + cardId);
                }
                if (progress + 1 == maxProgress) {
                    query = db.getReadableDatabase().query(
                            Tables.CARDS,
                            new String[] {
                                    CardsColumns.CARD_ID
                            }, CardsColumns.CARD_STACK_ID + " = " + stackId + " AND " +
                                    CardsColumns.CARD_PROGRESS + " = 0",
                            null, null, null, null, "1");
                    // if we have postponed card
                    if (query.moveToFirst()) {
                        long nextCardIdToLearn = query.getLong(0);
                        query.close();
                        values.clear();
                        values.put(CardsColumns.CARD_PROGRESS, 1);
                        db.getWritableDatabase().update(
                                Tables.CARDS,
                                values,
                                CardsColumns.CARD_ID + " = " + nextCardIdToLearn,
                                null);
                    } else {
                        String sql = "UPDATE " + Tables.STACKS + " SET " +
                                StacksColumns.STACK_COUNT_IN_LEARNING + " = " +
                                StacksColumns.STACK_COUNT_IN_LEARNING + " - 1 " +
                                "WHERE " + StacksColumns.STACK_ID + " = " + stackId;
                        db.getWritableDatabase().execSQL(sql);
                    }
                }
                return null;
            }
        });
    }

    @NonNull
    public Observable<Void> returnCard(final long cardId) {
        return makeObservable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ContentValues values = new ContentValues();
                values.put(CardsColumns.CARD_PROGRESS, 1);
                values.put(CardsColumns.CARD_NEXT_SHOWING,
                           System.currentTimeMillis() + PrefUtils.getSessionPeriod() * 3600000);
                int affected = db.getWritableDatabase().update(
                        Tables.CARDS,
                        values,
                        CardsColumns.CARD_ID + " = " + cardId,
                        null);
                if (0 == affected) {
                    throw new IllegalArgumentException("No card with id = " + cardId);
                }
                return null;
            }
        });
    }

    @NonNull
    public Observable<CursorWrapper<Card>> listCards(@Nullable final Long stackId,
                                                     @Nullable final String search) {
        return makeObservable(new Callable<CursorWrapper<Card>>() {
            @Override
            public CursorWrapper<Card> call() throws Exception {
                String selection = null;
                ArrayList<String> selectionArgs = new ArrayList<String>();
                if (null != stackId) {
                    selection = CardsColumns.CARD_STACK_ID + " = " + stackId;
                }
                if (null != search) {
                    String pattern = "%" + search + "%";
                    selection = DatabaseUtils.concatenateWhere(selection,
                            CardsColumns.CARD_FRONT + " LIKE ? OR " +
                            CardsColumns.CARD_BACK + " LIKE ?");
                    selectionArgs.add(pattern);
                    selectionArgs.add(pattern);
                }
                String[] selectionArgsArr = selectionArgs.toArray(new String[selectionArgs.size()]);
                Cursor query = db.getReadableDatabase().query(
                        Tables.CARDS, null, selection, selectionArgsArr, null, null, null);
                return new CursorWrapper<>(query, new Card.CardFactory());
            }
        });
    }

    @NonNull
    public Observable<List<Stack>> listStacks() {
        return makeObservable(new Callable<List<Stack>>() {
            @Override
            public List<Stack> call() throws Exception {
                Cursor query = db.getReadableDatabase().query(
                        Tables.STACKS, null, null, null, null, null, null);
                Stack.StackFactory factory = new Stack.StackFactory();
                List<Stack> result = new ArrayList<>(query.getCount());
                factory.prepare(query);
                while (query.moveToNext()) {
                    result.add(factory.wrapRow(query));
                }
                query.close();
                return result;
            }
        });
    }

    @NonNull
    public Observable<Statistics> getStatistics() {
        // todo: fill this after Statistics.java
        return Observable.empty();
    }

    @NonNull
    public Observable<CursorWrapper<Card>> getCardsToLearn(final long stackId) {
        return makeObservable(new Callable<CursorWrapper<Card>>() {
            @Override
            public CursorWrapper<Card> call() throws Exception {
                String selection = CardsColumns.CARD_NEXT_SHOWING + " < " + System.currentTimeMillis();
                selection = DatabaseUtils.concatenateWhere(selection,
                        CardsColumns.CARD_STACK_ID + " = " + stackId);
                Cursor query = db.getReadableDatabase().query(
                        Tables.CARDS, null, selection, null, null, null, null);
                return new CursorWrapper<>(query, new Card.CardFactory());
            }
        });
    }

    private void deleteCard(long cardId) {
        Cursor query = db.getReadableDatabase().query(
                Tables.CARDS,
                new String[]{
                        CardsColumns.CARD_PROGRESS,
                        CardsColumns.CARD_STACK_ID
                },
                CardsColumns.CARD_ID + " = " + cardId,
                null, null, null, null);
        if (!query.moveToFirst()) {
            throw new IllegalArgumentException("No card with id = " + cardId);
        }
        int progress = query.getInt(0);
        long stackId = query.getLong(1);
        query.close();
        int maxProgress = PrefUtils.getMaxProgress();

        SQLiteDatabase transaction = db.getWritableDatabase();
        transaction.beginTransaction();
        try {
            transaction.delete(
                    Tables.CARDS, CardsColumns.CARD_ID + " = " + cardId, null);

            query = db.getReadableDatabase().query(
                    Tables.CARDS,
                    new String[] {
                            CardsColumns.CARD_ID
                    }, CardsColumns.CARD_STACK_ID + " = " + stackId + " AND " +
                            CardsColumns.CARD_PROGRESS + " = 0",
                    null, null, null, null, "1");
            ContentValues values = new ContentValues();
            if (!query.moveToFirst()) {
                query.close();
                String sql = "UPDATE " + Tables.STACKS + " SET ";
                sql += StacksColumns.STACK_COUNT_CARDS + " = " +
                       StacksColumns.STACK_COUNT_CARDS + " - 1";
                if ((0 != progress) && (maxProgress != progress)) {
                    sql += ", " + StacksColumns.STACK_COUNT_IN_LEARNING + " = " +
                           StacksColumns.STACK_COUNT_IN_LEARNING + " - 1";
                }
                sql += " WHERE " + StacksColumns.STACK_ID + " = " + stackId;
                transaction.execSQL(sql);
            } else {
                long nextCardIdToLearn = query.getLong(0);
                query.close();
                values.put(CardsColumns.CARD_PROGRESS, 1);
                transaction.update(
                        Tables.CARDS,
                        values,
                        CardsColumns.CARD_ID + " = " + nextCardIdToLearn,
                        null);

                String sql = "UPDATE " + Tables.STACKS + " SET ";
                sql += StacksColumns.STACK_COUNT_CARDS + " = " +
                       StacksColumns.STACK_COUNT_CARDS + " - 1 ";
                sql += "WHERE " + StacksColumns.STACK_ID + " = " + stackId;
                transaction.execSQL(sql);
            }
            transaction.setTransactionSuccessful();
        } finally {
            transaction.endTransaction();
        }
    }

    @NonNull
    public Observable<Void> deleteCards(@NonNull final Iterable<Long> cardIds) {
        return makeObservable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (Long id : cardIds) {
                    deleteCard(id);
                }
                return null;
            }
        });
    }

    @NonNull
    public Observable<Void> deleteStack(final long stackId) {
        return makeObservable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                db.getWritableDatabase().delete(
                        Tables.STACKS, StacksColumns.STACK_ID + " = " + stackId, null);
                return null;
            }
        });
    }

    /**
     * Counts amount of cards that are ready to be learned.
     * @return Map: key is id of stack, value is amount of cards to learn.
     */
    @NonNull
    public Observable<Map<Long, Integer>> countCardsToLearn() {
        return makeObservable(new Callable<Map<Long, Integer>>() {
            @Override
            public Map<Long, Integer> call() throws Exception {
                Cursor query = db.getReadableDatabase().query(
                        Tables.CARDS,
                        new String[] { CardsColumns.CARD_STACK_ID, "count()" },
                        CardsColumns.CARD_NEXT_SHOWING + " < " + System.currentTimeMillis(),
                        null,
                        CardsColumns.CARD_STACK_ID,
                        null, null);
                Map<Long, Integer> result = new HashMap<>(query.getCount());
                while (query.moveToNext()) {
                    result.put(query.getLong(0), query.getInt(1));
                }
                query.close();
                return result;
            }
        });
    }
}
