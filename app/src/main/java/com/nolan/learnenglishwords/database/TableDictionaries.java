package com.nolan.learnenglishwords.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

public class TableDictionaries {
    public static final String TABLE_NAME = "DICTIONARIES";
    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";

    public static void Create(@NonNull SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY, " +
                TITLE + " TEXT, " +
                DESCRIPTION + " TEXT);";
        db.execSQL(sql);
    }

    public static void Upgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor dictionaries = db.query(TABLE_NAME, new String[]{ID}, null, null, null, null, null);
        long[] dictionaryIds = new long[dictionaries.getCount()];
        int i = 0;
        int idId = dictionaries.getColumnIndex(ID);
        while (dictionaries.moveToNext())
            dictionaryIds[i++] = dictionaries.getLong(idId);
        dictionaries.close();
        for (long id : dictionaryIds)
            TableDictionary.Upgrade(db, oldVersion, newVersion, id);
        // here upgrade for table DICTIONARIES
        // ...
    }
}
