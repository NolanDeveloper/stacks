package com.nolan.learnenglishwords.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public class TableDictionary {
    public static final String ID = "ID";
    public static final String FRONT = "FRONT";
    public static final String BACK = "BACK";
    public static final String LAST_ANSWER_TIME = "LAST_ANSWER_TIME";
    public static final String SCRUTINY = "SCRUTINY";

    public static void Create(@NonNull SQLiteDatabase db, long id) {
        String sql = "CREATE TABLE " + GetTableName(id) + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                FRONT + " TEXT, " +
                BACK + " TEXT, " +
                LAST_ANSWER_TIME + " TEXT, " +
                SCRUTINY + "INT DEFAULT 0, " +
                "UNIQUE (" + FRONT + ", " + BACK + "));";
        db.execSQL(sql);
    }

    public static String GetTableName(long id) {
        if (id < 0)
            throw new IllegalArgumentException("id must not be negative. (id  = " + Long.toString(id));
        return String.format("DICTIONARY_%d", id);
    }

    public static void Upgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion, long id) {

    }
}
