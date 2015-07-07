package com.nolan.learnenglishwords.controller.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.nolan.learnenglishwords.model.CardsContract;

/**
 * This activity is required to start different activities on launch by condition.
 * <p>
 * Another useful future function of this activity is initializing and downloading
 * some information on the start and showing app logo over float screen during this
 * process.
 */
public class LauncherActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(DictionariesQuery._TOKEN, null, this);
    }

    private interface DictionariesQuery {
        int _TOKEN = 0;

        // Columns which we need.
        String[] COLUMNS = {
                CardsContract.Dictionary.DICTIONARY_ID
        };

        int ID = 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CardsContract.Dictionary.CONTENT_URI, DictionariesQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query)
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        // The logic is simple. If there is no dictionaries we start CreateFirstDictionaryActivity
        // otherwise we start TrainingActivity.
        if (0 == query.getCount()) {
            Intent intent = new Intent(getBaseContext(), CreateFirstDictionaryActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
            query.moveToFirst();
            Uri data = ContentUris.withAppendedId(
                    CardsContract.Dictionary.CONTENT_URI, query.getLong(DictionariesQuery.ID));
            intent.setData(data);
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
