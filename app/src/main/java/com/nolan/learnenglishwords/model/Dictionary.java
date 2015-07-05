package com.nolan.learnenglishwords.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.annotation.NonNull;

import com.nolan.learnenglishwords.database.TableDictionaries;

import java.io.Serializable;

public class Dictionary implements Serializable {
    public final long id;
    public final String title;
    public final String description;

    public Dictionary(@NonNull Cursor cursor) {
        ContentValues contentValues = new ContentValues();
        int idId = cursor.getColumnIndex(TableDictionaries.ID);
        if (-1 != idId)
            id = cursor.getLong(idId);
        else
            id = -1;

        int idName = cursor.getColumnIndex(TableDictionaries.TITLE);
        if (-1 != idName)
            title = cursor.getString(idName);
        else
            title = null;

        int idDescription = cursor.getColumnIndex(TableDictionaries.DESCRIPTION);
        if (-1 != idDescription)
            description = cursor.getString(idDescription);
        else
            description = null;
    }
}