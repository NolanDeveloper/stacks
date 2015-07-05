package com.nolan.learnenglishwords.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nolan.learnenglishwords.database.DBOpenHelper;
import com.nolan.learnenglishwords.database.TableDictionaries;
import com.nolan.learnenglishwords.database.TableDictionary;
import com.nolan.learnenglishwords.model.Card;
import com.nolan.learnenglishwords.model.Dictionary;
import com.nolan.learnenglishwords.utils.DateStringConverter;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

public class BusinessLogic {
    public class NoCardsException extends Exception {
        public NoCardsException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static final String LOG_TAG = "BusinessLogic";

    public static final int MAX_DICTIONARY_TITLE_LEN = 15;
    public static final int MAX_DICTIONARY_DESCRIPTION_LEN = 50;
    public static final int MAX_FRONT_LEN = 25;
    public static final int MAX_BACK_LEN = MAX_FRONT_LEN;
    public static final int SCRUTINY_LEARNED = 20;

    private static BusinessLogic instance;
    private Context context;

    private boolean isInTraining;
    private long dictionaryId; // dictionary from which to take cards
    private Card visibleCard; // card that was taken
    private Random random;

    @NonNull
    public static BusinessLogic GetInstance(@NonNull Context context) {
        if (null == instance)
            instance = new BusinessLogic(context);
        return instance;
    }

    private BusinessLogic(@NonNull Context context) {
        this.context = context;
        this.isInTraining = false;
        this.random = new Random();
    }

    public long addDictionary(@NonNull String title, @Nullable String description) throws SQLException {
        Log.d(LOG_TAG, "addDictionary(" +
                title + ", " +
                (null == description ? "null" : description) + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if (null == description)
            description = "";
        if (MAX_DICTIONARY_TITLE_LEN < title.length())
            throw new IllegalArgumentException("title is too long. (len = " + Integer.toString(title.length()));
        if (MAX_DICTIONARY_DESCRIPTION_LEN < description.length())
            throw new IllegalArgumentException("description is too long. (len = " + Integer.toString(description.length()));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableDictionaries.TITLE, title);
        cv.put(TableDictionaries.DESCRIPTION, description);
        long id = db.insert(TableDictionaries.TABLE_NAME, null, cv); // add new dictionary to table of dictionaries
        TableDictionary.Create(db, id); // and create table for the new dictionary
        return id;
    }

    public long addCard(long id, @NonNull String front, @NonNull String back) throws SQLException {
        Log.d(LOG_TAG, "addCard(" +
                Long.toString(id) + ", " +
                front + ", " +
                back + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id = " + Long.toString(id));
        if (MAX_FRONT_LEN < front.length())
            throw new IllegalArgumentException("front is too long. (len = " + Integer.toString(front.length()));
        if (MAX_BACK_LEN < back.length())
            throw new IllegalArgumentException("back is too long. (len = " + Integer.toString(back.length()));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableDictionary.FRONT, front);
        cv.put(TableDictionary.BACK, back);
        cv.put(TableDictionary.LAST_ANSWER_TIME, DateStringConverter.toString(new Date()));
        return db.insert(TableDictionary.GetTableName(id), null, cv);
    }

    public void updateDictionary(long id, @Nullable String title, @Nullable String description) throws SQLException {
        Log.d(LOG_TAG, "updateDictionary(" +
                Long.toString(id) + ", " +
                (null == title ? "null" : title) + ", " +
                (null == description ? "null" : description) + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if ((null == title) && (null == description))
            throw new IllegalArgumentException("Both title and description are null. Nothing to update.");
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id = " + Long.toString(id));
        if ((null != title) &&
            (MAX_DICTIONARY_TITLE_LEN < title.length()))
            throw new IllegalArgumentException("title is too long. (len = " + Integer.toString(title.length()));
        if ((null != description) &&
            (MAX_DICTIONARY_DESCRIPTION_LEN < description.length()))
            throw new IllegalArgumentException("description is too long. (len = " + Integer.toString(description.length()));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        ContentValues cv = new ContentValues();
        if (null != title)
            cv.put(TableDictionaries.TITLE, title);
        if (null != description)
            cv.put(TableDictionaries.DESCRIPTION, description);
        db.update(TableDictionaries.TABLE_NAME, cv, TableDictionaries.ID + " = ?", new String[]{Long.toString(id)});
    }

    public void updateCard(long dictionaryId, long cardId, @Nullable String front, @Nullable String back) throws SQLException {
        Log.d(LOG_TAG, "updateCard(" +
                Long.toString(dictionaryId) + ", " +
                Long.toString(cardId) + ", " +
                (null == front ? "null" : front) + ", " +
                (null == back ? "null" : back) + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if ((null == front) && (null == back))
            throw new IllegalArgumentException("Both front and back are null. Nothing to update.");
        if (dictionaryId < 0)
            throw new IllegalArgumentException("dictionaryId must not be negative. (dictionaryId = " + Long.toString(dictionaryId));
        if (cardId < 0)
            throw new IllegalArgumentException("cardId must not be negative. (cardId = " + Long.toString(cardId));
        if ((null != front) &&
            (MAX_FRONT_LEN < front.length()))
            throw new IllegalArgumentException("front is too long. (len = " + Integer.toString(front.length()));
        if ((null != back) &&
            (MAX_BACK_LEN < back.length()))
            throw new IllegalArgumentException("description is too long. (len = " + Integer.toString(back.length()));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        ContentValues cv = new ContentValues();
        if (null != front)
            cv.put(TableDictionary.FRONT, front);
        if (null != back)
            cv.put(TableDictionary.BACK, back);
        db.update(TableDictionary.GetTableName(dictionaryId), cv, TableDictionary.ID + " = ?", new String[]{Long.toString(cardId)});
    }

    public void removeDictionary(long id) throws SQLException {
        Log.d(LOG_TAG, "removeDictionary(" + Long.toString(id) + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id = " + Long.toString(id));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DROP TABLE " + TableDictionary.GetTableName(id));
            db.delete(TableDictionaries.TABLE_NAME, TableDictionaries.ID + " = ?", new String[]{Long.toString(id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void removeCard(long dictionaryId, long cardId) throws SQLException {
        Log.d(LOG_TAG, "removeCard(" + Long.toString(dictionaryId) + ", " + Long.toString(cardId) + ")");
        if (isInTraining)
            throw new IllegalStateException("You are in training. Use stopCardTraining() to stop training. Yours cap.");
        if (dictionaryId < 0)
            throw new IllegalArgumentException("dictionaryId must not be negative. (dictionaryId = " + Long.toString(dictionaryId));
        if (cardId < 0)
            throw new IllegalArgumentException("cardId must not be negative. (cardId = " + Long.toString(cardId));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        db.delete(TableDictionary.GetTableName(dictionaryId), TableDictionary.ID + " = ?", new String[]{Long.toString(cardId)});
    }

    public Cursor queryDictionaries() throws SQLException {
        Log.d(LOG_TAG, "queryDictionaries()");
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        return db.query(TableDictionaries.TABLE_NAME, null, null, null, null, null, null);
    }

    public Dictionary queryDictionary(long id) throws SQLException {
        Log.d(LOG_TAG, "queryDictionary(" + Long.toString(id) + ")");
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        Cursor dictionaries = db.query(TableDictionaries.TABLE_NAME, null, TableDictionaries.ID + " = ?", new String[]{Long.toString(id)}, null, null, null);
        if (0 == dictionaries.getCount()) {
            dictionaries.close();
            return null;
        }
        dictionaries.moveToFirst();
        Dictionary result = new Dictionary(dictionaries);
        dictionaries.close();
        return result;
    }

    public Cursor queryCards(long id) throws SQLException {
        Log.d(LOG_TAG, "queryCards(" + Long.toString(id) + ")");
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id = " + Long.toString(id));
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        return db.query(TableDictionary.GetTableName(id), null, null, null, null, null, null);
    }

    public void startCardTraining(long id) throws SQLException, NoCardsException {
        Log.d(LOG_TAG, "startCardTraining(" + Long.toString(id) + ")");
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id = " + Long.toString(id));
        Cursor cards = queryCards(id);
        int count = cards.getCount();
        cards.close();
        if (0 == count)
            throw new NoCardsException("There are no cards in the dictionary.");
        dictionaryId = id;
        isInTraining = true;
    }

    public boolean guess(@NonNull String back) throws SQLException {
        Log.d(LOG_TAG, "guess(" + back + ")");
        if (!isInTraining)
            throw new IllegalStateException("Call startCardTraining() before call guess().");
        if (null == visibleCard)
            throw new IllegalStateException("Between each two calls of guess() nextCard() must be called.");
        boolean result = back.equalsIgnoreCase(visibleCard.back);
        long timeDiff = new Date().getTime() - visibleCard.lastAnswer.getTime();
        final long millisecondsInDay = 1000 * 60 * 60 * 24;
        if (millisecondsInDay < timeDiff) {
            SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
            if (result && (SCRUTINY_LEARNED == visibleCard.scrutiny + 1)) // if learned remove from dictionary
                db.delete(TableDictionary.GetTableName(dictionaryId), TableDictionary.ID + " = ?", new String[]{Long.toString(visibleCard.id)});
            else { // otherwise update scrutiny
                ContentValues cv = new ContentValues();
                cv.put(TableDictionary.SCRUTINY, visibleCard.scrutiny + (result ? 1 : -1));
                db.update(TableDictionary.GetTableName(dictionaryId), cv, TableDictionary.ID + " = ?", new String[]{Long.toString(visibleCard.id)});
            }
        }
        visibleCard = null;
        return result;
    }

    public String nextCard() throws SQLException {
        Log.d(LOG_TAG, "nextCard()");
        if (!isInTraining)
            throw new IllegalStateException("Call startCardTraining() before call nextCard().");
        SQLiteDatabase db = DBOpenHelper.GetInstance(context).getReadableDatabase();
        Cursor allCards = db.query(TableDictionary.GetTableName(dictionaryId), null, null, null, null, null, "datetime(" + TableDictionary.LAST_ANSWER_TIME + ") DESC");
        // the rule is those cards with old last answer
        // should occur more probably than others
        double randDoubleMinOneSqr = random.nextDouble() - 1;
        randDoubleMinOneSqr *= randDoubleMinOneSqr;
        int randomPosition = (int)(randDoubleMinOneSqr * allCards.getCount());
        allCards.moveToPosition(randomPosition);
        visibleCard = new Card(allCards);
        allCards.close();
        return visibleCard.front;
    }

    public void stopCardTraining() {
        Log.d(LOG_TAG, "stopCardTraining()");
        if (!isInTraining)
            throw new IllegalStateException("You cannot stopCardTraining() if you did not start it.");
        visibleCard = null;
        dictionaryId = -1;
        isInTraining = false;
    }

    public boolean isInTraining() {
        return isInTraining;
    }
}
