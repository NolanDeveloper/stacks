package com.nolane.stacks.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

import com.nolane.stacks.model.CardsContract.*;

public class CardsProvider extends ContentProvider {
    // Tag for logging. (e.g. Log.i(LOG_TAG, "all right!"))
    private static final String LOG_TAG = CardsProvider.class.getName();

    // Codes for uri matcher.
    private static final int DICTIONARIES_TABLE = 100;
    private static final int DICTIONARIES_ID = 101;

    private static final int CARDS_TABLE = 200;
    private static final int CARDS_OF_DICTIONARY = 201;
    private static final int CARDS_ID = 202;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        final String authority = CardsContract.CONTENT_AUTHORITY;
        URI_MATCHER.addURI(authority, CardsContract.PATH_DICTIONARIES, DICTIONARIES_TABLE);
        URI_MATCHER.addURI(authority, CardsContract.PATH_DICTIONARIES + "/#", DICTIONARIES_ID);
        URI_MATCHER.addURI(authority, CardsContract.PATH_CARDS, CARDS_TABLE);
        URI_MATCHER.addURI(authority, CardsContract.PATH_CARDS + "/#", CARDS_OF_DICTIONARY);
        URI_MATCHER.addURI(authority, CardsContract.PATH_CARDS + "/#/#", CARDS_ID);
    }

    private CardsDatabase db;

    @Override
    public boolean onCreate() {
        db = new CardsDatabase(getContext());
        try {
            db.getWritableDatabase();
            Log.d(LOG_TAG, "Provider was created.");
            return true;
        } catch (SQLiteException e) {
            // This can happen for example if there is no free memory. This case definitely
            // must be tested but I don't know any way to do it except of making tons of
            // garbage in memory of my phone which is definitely wrong.
            // fixme: create test case
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "Query");
        Log.d(LOG_TAG, "uri: " + uri.toString());
        Log.d(LOG_TAG, "projection: " + ((null != projection) ? Arrays.deepToString(projection) : "null"));
        Log.d(LOG_TAG, "selection: " + selection);
        Log.d(LOG_TAG, "selectionArgs: " + ((null != selectionArgs) ? Arrays.deepToString(selectionArgs) : "null"));
        Log.d(LOG_TAG, "sortOrder: " + sortOrder);

        switch (URI_MATCHER.match(uri)) {
            case UriMatcher.NO_MATCH:
                return null;
            case DICTIONARIES_TABLE:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = Dictionary.SORT_DEFAULT;
                break;
            case DICTIONARIES_ID:
            case CARDS_ID: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, BaseColumns._ID + " = " + id);
                break;
            } case CARDS_OF_DICTIONARY: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, Card.CARD_DICTIONARY_ID + " = " + id);
            }
        }
        // The first part of uri is always table name. In other words all table
        // names are at the top of uri hierarchy providing by #URI_MATCHER.
        String table = uri.getPathSegments().get(0);
        Cursor cursor = db.getReadableDatabase().query(table, projection, selection, selectionArgs, null, null, sortOrder);
        if ((null != cursor) && (null != getContext()))
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case UriMatcher.NO_MATCH:
                return null;
            case DICTIONARIES_TABLE:
                return Dictionary.CONTENT_TYPE;
            case DICTIONARIES_ID:
                return Dictionary.CONTENT_ITEM_TYPE;
            case CARDS_TABLE:
                return Card.CONTENT_TYPE;
            case CARDS_OF_DICTIONARY:
                return Card.CONTENT_TYPE;
            case CARDS_ID:
                return Card.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "Insert");
        Log.d(LOG_TAG, "uri: " + uri.toString());
        Log.d(LOG_TAG, "values: " + ((null != values) ? values.toString() : "null"));

        checkOrThrow(uri, values);
        switch (URI_MATCHER.match(uri)) {
            case UriMatcher.NO_MATCH:
                return null;
            case DICTIONARIES_ID:
            case CARDS_ID:
                throw new IllegalArgumentException("Specified uri points to the row of table. You can't insert into row.");
            case CARDS_OF_DICTIONARY:
                if (values == null)
                    values = new ContentValues();
                values.put(Card.CARD_DICTIONARY_ID, uri.getLastPathSegment());
                break;
        }
        String table = uri.getPathSegments().get(0);
        long id = db.getWritableDatabase().insert(table, null, values);
        if (-1 == id)
            return null;
        uri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "Delete");
        Log.d(LOG_TAG, "uri: " + uri.toString());
        Log.d(LOG_TAG, "selection: " + selection);
        Log.d(LOG_TAG, "selectionArgs: " + ((null != selectionArgs) ? Arrays.deepToString(selectionArgs) : "null"));

        switch (URI_MATCHER.match(uri)) {
            case UriMatcher.NO_MATCH:
                return 0;
            case DICTIONARIES_ID:
            case CARDS_ID: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, BaseColumns._ID + " = " + id);
                break;
            } case CARDS_OF_DICTIONARY: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, Card.CARD_DICTIONARY_ID + " = " + id);
                break;
            }
        }
        String table = uri.getPathSegments().get(0);
        int count = db.getReadableDatabase().delete(table, selection, selectionArgs);
        if (0 < count)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "Update");
        Log.d(LOG_TAG, "uri: " + uri.toString());
        Log.d(LOG_TAG, "values: " + ((null != values) ? values.toString() : "null"));
        Log.d(LOG_TAG, "selection: " + selection);
        Log.d(LOG_TAG, "selectionArgs: " + ((null != selectionArgs) ? Arrays.deepToString(selectionArgs) : "null"));

        checkOrThrow(uri, values);
        switch (URI_MATCHER.match(uri)) {
            case UriMatcher.NO_MATCH:
                return 0;
            case DICTIONARIES_ID:
            case CARDS_ID: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, BaseColumns._ID + " = " + id);
                break;
            } case CARDS_OF_DICTIONARY: {
                String id = uri.getLastPathSegment();
                selection = DatabaseUtils.concatenateWhere(selection, Card.CARD_DICTIONARY_ID + " = " + id);
                break;
            }
        }
        String table = uri.getPathSegments().get(0);
        int count = db.getReadableDatabase().update(table, values, selection, selectionArgs);
        if (0 < count)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * Checks values before updating and inserting and throws {@code IllegalArgumentException}
     * in case of any mistake in parameters.
     * @param uri Uri argument of {@link #insert} or {@link #update}.
     * @param values ContentValues argument of {@link #insert} or {@link #update}.
     * @throws IllegalArgumentException This is thrown in case of failing check. It also provides
     * message describing the reason of failing.
     */
    private void checkOrThrow(Uri uri, ContentValues values) throws IllegalArgumentException {
        switch (URI_MATCHER.match(uri)) {
            case DICTIONARIES_TABLE:
            case DICTIONARIES_ID:
                String title = values.getAsString(DictionariesColumns.DICTIONARY_TITLE);
                if (null != title && !Dictionary.checkTitle(title))
                    throw new IllegalArgumentException("The title is too long. (max len is " + Dictionary.MAX_TITLE_LEN + ")");
                String description = values.getAsString(DictionariesColumns.DICTIONARY_DESCRIPTION);
                if (null != description && !Dictionary.checkDescription(description))
                    throw new IllegalArgumentException("The description is too long. (max len is " + Dictionary.MAX_DESCRIPTION_LEN + ")");
                break;
            case CARDS_TABLE:
            case CARDS_ID:
                String front = values.getAsString(CardsColumns.CARD_FRONT);
                if (null != front && !Card.checkFront(front))
                    throw new IllegalArgumentException("The front is too long. (max len is " + Card.MAX_FRONT_LEN + ")");
                String back = values.getAsString(CardsColumns.CARD_FRONT);
                if (null != back && !Card.checkBack(back))
                    throw new IllegalArgumentException("The back is too long. (max len is " + Card.MAX_BACK_LEN + ")");
                break;
            default:
                throw new IllegalArgumentException("Can't check unknown uri.");
        }
    }
}
