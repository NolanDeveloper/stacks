package com.nolan.learnenglishwords.activities;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.fragments.AddCardFragment;
import com.nolan.learnenglishwords.model.Dictionary;
import com.nolan.learnenglishwords.utils.AsyncResult;

public class AddCardActivity extends BaseNavigationDrawerActivity implements LoaderManager.LoaderCallbacks<Object> {
    public static class DictionaryLoader extends AsyncTaskLoader<Object> {
        public static final String ARG_DICTIONARY_ID = "dictionary id";

        private long id;
        private BusinessLogic businessLogic;

        public DictionaryLoader(Context context, long id) {
            super(context);
            this.id = id;
            this.businessLogic = BusinessLogic.GetInstance(context);
        }

        @Override
        public AsyncResult<Dictionary> loadInBackground() {
            try {
                return new AsyncResult<>(businessLogic.queryDictionary(id));
            } catch (Throwable e) {
                return new AsyncResult<>(e);
            }
        }
    }

    public static final String ARG_DICTIONARY_ID = "dictionary id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long id = getIntent().getLongExtra(ARG_DICTIONARY_ID, -1);
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_DICTIONARY_ID, id);
        getLoaderManager().initLoader(0, arguments, this);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new DictionaryLoader(this, args.getLong("id"));
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        setMainFragment(AddCardFragment.GetInstance((Dictionary) data));
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
