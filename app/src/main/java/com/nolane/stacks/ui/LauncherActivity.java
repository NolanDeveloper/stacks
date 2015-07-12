package com.nolane.stacks.ui;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity is required to start different activities on launch by condition.
 * <p>
 * Another useful future function of this activity is initializing and downloading
 * some information on the start and showing app logo over float screen during this
 * process.
 */
public class LauncherActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_launcher);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar && actionBar.isShowing()) actionBar.hide();
        // todo: show big logo
        getLoaderManager().initLoader(StacksQuery._TOKEN, null, this);
    }

    private interface StacksQuery {
        int _TOKEN = 0;

        // Columns which we need.
        String[] COLUMNS = {
                CardsContract.Stacks.STACK_ID
        };

        int ID = 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CardsContract.Stacks.CONTENT_URI, StacksQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        // The logic is simple. If there is no stacks we start CreateFirstStackActivity
        // otherwise we start TrainingActivity.
        if (0 == query.getCount()) {
            Intent intent = new Intent(getBaseContext(), CreateFirstStackActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getBaseContext(), PickStackActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
