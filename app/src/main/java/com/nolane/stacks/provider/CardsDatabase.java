package com.nolane.stacks.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.nolane.stacks.provider.CardsContract.AnswersColumns;
import static com.nolane.stacks.provider.CardsContract.CardsColumns;
import static com.nolane.stacks.provider.CardsContract.StacksColumns;

/**
 * This class provides convenient database access. It's used by {@link CardsProvider}
 * and shouldn't be used elsewhere.
 */
public class CardsDatabase extends SQLiteOpenHelper {
    // The name of the database.
    public static final String DATABASE_NAME = "cards.db";

    // Current version of data base. It must be updated for each new scheme.
    // We can use this value to correctly update db in #onUpdate.
    public static final int CURRENT_VERSION = 1;

    // We need to get preferences.
    private Context context;

    // Contractions for Tables. It is easier to keep in memory.
    interface Tables {
        String STACKS = CardsContract.PATH_STACKS;
        String CARDS = CardsContract.PATH_CARDS;
        String ANSWERS = CardsContract.PATH_ANSWERS;
    }

    // Contractions for REFERENCES clause.
    interface References {
        String STACKS_ID = "REFERENCES " + Tables.STACKS + "(" + StacksColumns.STACK_ID + ") ON DELETE CASCADE";
        String CARDS_ID = "REFERENCES " + Tables.CARDS + "(" + CardsColumns.CARD_ID + ") ON DELETE CASCADE";
    }

    public CardsDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.STACKS + "(" +
                StacksColumns.STACK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StacksColumns.STACK_TITLE + " TEXT NOT NULL, " +
                StacksColumns.STACK_MAX_IN_LEARNING + " INTEGER NOT NULL," +
                StacksColumns.STACK_COUNT_CARDS + " INTEGER DEFAULT 0 NOT NULL, " +
                StacksColumns.STACK_COUNT_IN_LEARNING + " INTEGER DEFAULT 0 NOT NULL, " +
                StacksColumns.STACK_LANGUAGE + " TEXT NOT NULL," +
                StacksColumns.STACK_COLOR + " INTEGER NOT NULL," +
                StacksColumns.STACK_DELETED + " INTEGER DEFAULT 0 NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.CARDS + "(" +
                CardsColumns.CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardsColumns.CARD_FRONT + " TEXT NOT NULL, " +
                CardsColumns.CARD_BACK + " TEXT NOT NULL, " +
                CardsColumns.CARD_PROGRESS + " INTEGER DEFAULT 0 NOT NULL, " +
                CardsColumns.CARD_LAST_SEEN + " TEXT DEFAULT 0 NOT NULL, " +
                CardsColumns.CARD_STACK_ID + " INTEGER " + References.STACKS_ID + " , " +
                CardsColumns.CARD_IN_LEARNING + " INTEGER NOT NULL," +
                CardsColumns.CARD_DELETED + " INTEGER DEFAULT 0 NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.ANSWERS + "(" +
                AnswersColumns.ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AnswersColumns.ANSWER_CARD_ID + " INTEGER " + References.CARDS_ID + " , " +
                AnswersColumns.ANSWER_TIMESTAMP + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                AnswersColumns.ANSWER_RIGHT + " INTEGER NOT NULL," +
                AnswersColumns.ANSWER_DELETED + " INTEGER DEFAULT 0 NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is only one version of this db for now. Nowhere to update.
    }
}
