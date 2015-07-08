package com.nolane.stacks.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nolane.stacks.provider.CardsContract.*;

public class CardsDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cards";

    // Current version of data base. It must be updated for each new scheme.
    // We can use this value to correctly update db in #onUpdate.
    public static final int CURRENT_VERSION = 1;

    // Contractions for Tables. It is easier to keep in memory.
    interface Tables {
        String STACKS = CardsContract.PATH_STACKS;
        String CARDS = CardsContract.PATH_CARDS;
    }

    // Contractions for REFERENCES clause.
    interface References {
        String STACKS_ID = "REFERENCES " + Tables.STACKS + "(" + StacksColumns.STACK_ID + ")";
    }

    public CardsDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
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
                StacksColumns.STACK_DESCRIPTION + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.CARDS + "(" +
                CardsColumns.CARD_ID + " INTEGER PRIMARY KEY, " +
                CardsColumns.CARD_FRONT + " TEXT NOT NULL, " +
                CardsColumns.CARD_BACK + " TEXT NOT NULL, " +
                CardsColumns.CARD_SCRUTINY + " INTEGER DEFAULT 0, " +
                CardsColumns.CARD_LAST_SEEN + " INTEGER DEFAULT 0, " +
                CardsColumns.CARD_STACK_ID + " INTEGER " + References.STACKS_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is only one version of this db for now. Nowhere to update.
    }
}
