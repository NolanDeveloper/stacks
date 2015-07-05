package com.nolan.learnenglishwords.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.nolan.learnenglishwords.database.TableDictionary;
import com.nolan.learnenglishwords.utils.DateStringConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.IllegalFormatException;

public class Card implements Serializable {
    public final long id;
    public final String front;
    public final String back;
    public final Date lastAnswer;
    public final int scrutiny;

    public Card(@NonNull Cursor c) throws IllegalFormatException {
        int idId = c.getColumnIndex(TableDictionary.ID);
        if (-1 != idId)
            id = c.getLong(idId);
        else
            id = -1;

        int idFront = c.getColumnIndex(TableDictionary.FRONT);
        if (-1 != idFront)
            front = c.getString(idFront);
        else
            front = null;

        int idBack = c.getColumnIndex(TableDictionary.BACK);
        if (-1 != idBack)
            back = c.getString(idBack);
        else
            back = null;

        int idLastAnswer = c.getColumnIndex(TableDictionary.LAST_ANSWER_TIME);
        if (-1 != idLastAnswer) {
            String lastAnswerText = c.getString(idLastAnswer);
            lastAnswer = DateStringConverter.parse(lastAnswerText);
        } else
            lastAnswer = null;

        int idScrutiny = c.getColumnIndex(TableDictionary.SCRUTINY);
        if (-1 != idScrutiny)
            scrutiny = c.getInt(idScrutiny);
        else
            scrutiny = Integer.MIN_VALUE;
    }
}
