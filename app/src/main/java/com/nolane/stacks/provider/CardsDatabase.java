package com.nolane.stacks.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract.*;

public class CardsDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cards";

    // Current version of data base. It must be updated for each new scheme.
    // We can use this value to correctly update db in #onUpdate.
    public static final int CURRENT_VERSION = 1;

    // We need to get preferences.
    private Context context;

    // Contractions for Tables. It is easier to keep in memory.
    interface Tables {
        String STACKS = CardsContract.PATH_STACKS;
        String CARDS = CardsContract.PATH_CARDS;
    }

    // Contractions for REFERENCES clause.
    interface References {
        String STACKS_ID = "REFERENCES " + Tables.STACKS + "(" + StacksColumns.STACK_ID + ") ON DELETE CASCADE";
    }

    interface Triggers {
        String CARDS_UPDATE = "CARDS_UPDATE";
    }

    public CardsDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.STACKS + "(" +
                StacksColumns.STACK_ID + " INTEGER PRIMARY KEY, " +
                StacksColumns.STACK_TITLE + " TEXT NOT NULL, " +
                StacksColumns.STACK_DESCRIPTION + " TEXT NOT NULL, " +
                StacksColumns.STACK_MAX_IN_LEARNING + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.CARDS + "(" +
                CardsColumns.CARD_ID + " INTEGER PRIMARY KEY, " +
                CardsColumns.CARD_FRONT + " TEXT NOT NULL, " +
                CardsColumns.CARD_BACK + " TEXT NOT NULL, " +
                CardsColumns.CARD_SCRUTINY + " INTEGER DEFAULT 0, " +
                CardsColumns.CARD_LAST_SEEN + " INTEGER DEFAULT 0, " +
                CardsColumns.CARD_STACK_ID + " INTEGER " + References.STACKS_ID + " ," +
                CardsColumns.CARD_IN_LEARNING + " INTEGER NOT NULL)");

        int defaultMaxScrutiny = context.getResources().getInteger(R.integer.default_max_scrutiny);
        setMaxScrutinyTrigger(db, defaultMaxScrutiny);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is only one version of this db for now. Nowhere to update.
    }

    /**
     * This method updates trigger which listens for updates on cards table to
     * unset flag "in learning" when scrutiny becomes maximum. But the trigger
     * requires this max value. So use this method each time the maximum scrutiny
     * value is changed.
     * @param value The maximum scrutiny.
     */
    public void setMaxScrutinyTrigger(@NonNull SQLiteDatabase db, int value) {
        db.beginTransaction();
        try {
            db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.CARDS_UPDATE);
            db.execSQL("CREATE TRIGGER " + Triggers.CARDS_UPDATE +
                    " AFTER UPDATE OF " + CardsColumns.CARD_SCRUTINY +
                    " ON " + Tables.CARDS +
                    " WHEN NEW." + CardsColumns.CARD_SCRUTINY + " = " + value +
                    " BEGIN" +
                    // Unset flag "in learning".
                    " UPDATE " + Tables.CARDS +
                        " SET " + CardsColumns.CARD_IN_LEARNING + " = 0" +
                        " WHERE " + CardsColumns.CARD_ID + " = NEW." + CardsColumns.CARD_ID + ";" +
                    // And find another card to set "in learning".
                    " UPDATE " + Tables.CARDS +
                        " SET " + CardsColumns.CARD_IN_LEARNING + " = 1" +
                        " WHERE " + CardsColumns.CARD_ID + " = " +
                            "(SELECT " + CardsColumns.CARD_ID + " FROM " + Tables.CARDS +
                            " WHERE " + CardsColumns.CARD_SCRUTINY + " != " + value +
                            " ORDER BY " + CardsColumns.CARD_LAST_SEEN + " ASC " +
                            " LIMIT 1);" +
                    " END");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
