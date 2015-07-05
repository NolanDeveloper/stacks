package com.nolan.learnenglishwords.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class DBOpenHelper extends SQLiteOpenHelper {
    public static final String NAME = "database";
    private static final int VERSION = 1;
    private static DBOpenHelper instance;

    private DBOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static DBOpenHelper GetInstance(@NonNull Context context) {
        if (null == instance)
            instance = new DBOpenHelper(context);
        return instance;
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        TableDictionaries.Create(db);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        TableDictionaries.Upgrade(db, oldVersion, newVersion);
    }
}
