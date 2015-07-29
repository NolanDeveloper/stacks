package com.nolane.stacks.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class provides database access for cards, stacks and answers.
 */
public class CardsDatabase extends SQLiteOpenHelper {
    // The name of the database.
    public static final String DATABASE_NAME = "cards.db";

    // Current version of data base. It must be updated for each new scheme.
    // We can use this value to correctly update db in #onUpdate.
    public static final int CURRENT_VERSION = 1;

    /*
    Stack is the big group of cards that user wants to separate from other ones.
    Usually he will have only one stack. But he can create as many as he wish.
    For the sake of clarity it's necessary to say that stacks aren't boxes.
    Box is just sign of progress for card. Each stack has N boxes and boxes
    aren't shared among stacks.
     */
    public interface StacksColumns {
        // The id of column.
        String STACK_ID = "STACK_ID";
        // The Title.
        String STACK_TITLE = "STACK_TITLE";
        // Maximum of cards in "in learning" state.
        String STACK_MAX_IN_LEARNING = "STACK_MAX_IN_LEARNING";
        // The amount of cards in this stack.
        String STACK_COUNT_CARDS = "STACK_COUNT_CARDS";
        // The amount of cards in learning stage.
        String STACK_COUNT_IN_LEARNING = "STACK_COUNT_IN_LEARNING";
        // The language of this stack.
        String STACK_LANGUAGE = "STACK_LANGUAGE";
        // The color associated with this stack. Integer value.
        String STACK_COLOR = "STACK_COLOR";
    }

    /*
    Cards are objects that has front and back inscriptions. Front one is shown to
    user. Back one is guessed by user.
     */
    public interface CardsColumns {
        // Id column.
        String CARD_ID = "CARD_ID";
        // Front text.
        String CARD_FRONT = "CARD_FRONT";
        // Back text.
        String CARD_BACK = "CARD_BACK";
        // The number of box where this card is.
        // 0 - means card is out of learning.
        // N is amount of boxes
        // 1..N - means card is in learning.
        // N+1 - means card is learned.
        String CARD_PROGRESS = "CARD_PROGRESS";
        // Unix time when this card is going to be shown.
        String CARD_NEXT_SHOWING = "CARD_NEXT_SHOWING";
        // Id of the stack this card belongs to.
        String CARD_STACK_ID = "CARD_STACK_ID";
    }

    public interface AnswersColumns {
        // Id columns.
        String ANSWER_ID = "ANSWER_ID";
        // Id of card which was answered.
        String ANSWER_CARD_ID = "ANSWER_CARD_ID";
        // Unix time when answer was done.
        String ANSWER_TIME = "ANSWER_TIME";
        // User's assumption.
        String ANSWER_RIGHT = "ANSWER_RIGHT";
    }

    // Contractions for Tables. It is easier to keep in memory.
    public interface Tables {
        String STACKS = "STACKS";
        String CARDS = "CARDS";
        String ANSWERS = "ANSWERS";
    }

    // Contractions for REFERENCES clause.
    interface References {
        String STACKS_ID = "REFERENCES " + Tables.STACKS + "(" + StacksColumns.STACK_ID + ") ON DELETE CASCADE";
        String CARDS_ID = "REFERENCES " + Tables.CARDS + "(" + CardsColumns.CARD_ID + ") ON DELETE CASCADE";
    }

    public CardsDatabase(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
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
                StacksColumns.STACK_COLOR + " INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.CARDS + "(" +
                CardsColumns.CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CardsColumns.CARD_FRONT + " TEXT NOT NULL, " +
                CardsColumns.CARD_BACK + " TEXT NOT NULL, " +
                CardsColumns.CARD_PROGRESS + " INTEGER DEFAULT 0 NOT NULL, " +
                CardsColumns.CARD_NEXT_SHOWING + " INTEGER NOT NULL, " +
                CardsColumns.CARD_STACK_ID + " INTEGER " + References.STACKS_ID + ")");

        db.execSQL("CREATE TABLE " + Tables.ANSWERS + "(" +
                AnswersColumns.ANSWER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AnswersColumns.ANSWER_CARD_ID + " INTEGER " + References.CARDS_ID + " , " +
                AnswersColumns.ANSWER_TIME + " LONG NOT NULL, " +
                AnswersColumns.ANSWER_RIGHT + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // There is only one version of this db for now. Nowhere to update.
    }
}
