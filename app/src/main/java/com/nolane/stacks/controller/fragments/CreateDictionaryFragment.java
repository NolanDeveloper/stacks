package com.nolane.stacks.controller.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nolane.stacks.R;
import com.nolane.stacks.controller.activities.AddCardActivity;
import com.nolane.stacks.controller.activities.CreateDictionaryActivity;
import com.nolane.stacks.controller.activities.CreateFirstDictionaryActivity;
import com.nolane.stacks.model.CardsContract;

/**
 * This fragment is for creating new dictionaries.
 * <p>
 * {@link CreateDictionaryActivity} and {@link CreateFirstDictionaryActivity} use this.
 */
public class CreateDictionaryFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Uri> {
    // UI elements.
    private EditText etTitle;
    private EditText etDescription;
    private Button btnDone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_dictionary_fragment, container, false);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etDescription = (EditText) view.findViewById(R.id.et_description);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        btnDone.setOnClickListener(this);

        if (null == savedInstanceState) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(CardsContract.Dictionary.MAX_TITLE_LEN);
            etTitle.setFilters(filterArray);

            filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(CardsContract.Dictionary.MAX_DESCRIPTION_LEN);
            etDescription.setFilters(filterArray);
        }
        return view;
    }

    private static final String VALUES = "values";

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        // If you know better way to pass values though Bundle please,
        // make pull request on https://github.com/Nolane/learn-english-words
        Bundle args = new Bundle();
        ContentValues values = new ContentValues();
        values.put(CardsContract.Dictionary.DICTIONARY_TITLE, title);
        values.put(CardsContract.Dictionary.DICTIONARY_DESCRIPTION, description);
        args.putParcelable(VALUES, values);
        getLoaderManager().initLoader(0, args, this).forceLoad();
    }

    @Override
    public Loader<Uri> onCreateLoader(int id, Bundle args) {
        // Here we use AsyncTaskLoader instead of simple AsyncTask because we need
        // to start another activity after inserting even if this activity was recreated during
        // inserting.
        final ContentValues values = args.getParcelable(VALUES);
        return new AsyncTaskLoader<Uri>(getActivity()) {
            @Override
            public Uri loadInBackground() {
                return getContext().getContentResolver().insert(CardsContract.Dictionary.CONTENT_URI, values);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Uri> loader, Uri uri) {
        if (null == uri)
            throw new IllegalArgumentException("Loader was failed. (uri = null)");
        Intent intent = new Intent(getActivity().getBaseContext(), AddCardActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {

    }
}
