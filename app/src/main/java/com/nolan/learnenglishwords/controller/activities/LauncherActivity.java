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

public class LauncherActivity extends Activity implements LoaderManager.LoaderCallbacks<Object> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CardsContract.Dictionary.CONTENT_URI,
                new String[]{
                        CardsContract.Dictionary.DICTIONARY_ID,
                }, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        Cursor query = (Cursor) data;
        if (0 == query.getCount()) {
            Intent intent = new Intent(getBaseContext(), CreateFirstDictionaryActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
            query.moveToFirst();
            Uri activityData = ContentUris.withAppendedId(CardsContract.Dictionary.CONTENT_URI, query.getLong(0));
            intent.setData(activityData);
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
