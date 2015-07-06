package com.nolan.learnenglishwords.controller.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.model.CardsContract;

public class AddCardFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Object> {
    private Uri dictionary;

    private long dictionaryId;
    private String dictionaryTitle;

    private TextView tvTitle;
    private EditText etFront;
    private EditText etBack;
    private Button btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionary = getActivity().getIntent().getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_card_fragment, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            getLoaderManager().initLoader(DictionaryQuery.TOKEN, null, this).forceLoad();
    }

    @Override
    public void onClick(View v) {
        String front = etFront.getText().toString();
        String back = etBack.getText().toString();
        final ContentResolver resolver = getActivity().getContentResolver();
        final ContentValues values = new ContentValues();
        values.put(CardsContract.Card.CARD_FRONT, front);
        values.put(CardsContract.Card.CARD_BACK, back);
        values.put(CardsContract.Card.CARD_DICTIONARY_ID, dictionaryId);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                resolver.insert(CardsContract.Card.CONTENT_URI, values);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getActivity(), getString(R.string.added), Toast.LENGTH_SHORT).show();
            }
        }.execute();

        etFront.getText().clear();
        etBack.getText().clear();
    }

    private interface DictionaryQuery {
        int TOKEN = 0;

        String[] COLUMNS = {
                CardsContract.Dictionary.DICTIONARY_ID,
                CardsContract.Dictionary.DICTIONARY_TITLE
        };

        int ID = 0;
        int TITLE = 1;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), dictionary, DictionaryQuery.COLUMNS, null, null, CardsContract.Dictionary.SORT_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        Cursor query = (Cursor) data;
        query.moveToFirst();
        dictionaryId = query.getLong(DictionaryQuery.ID);
        dictionaryTitle = query.getString(DictionaryQuery.TITLE);
        initUi();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    private void initUi() {
        tvTitle.setText(dictionaryTitle);
        btnDone.setOnClickListener(this);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(CardsContract.Card.MAX_FRONT_LEN);
        etFront.setFilters(filters);
        filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(CardsContract.Card.MAX_BACK_LEN);
        etBack.setFilters(filters);
    }
}
