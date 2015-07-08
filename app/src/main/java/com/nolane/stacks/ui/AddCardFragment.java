package com.nolane.stacks.ui;

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

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;

/**
 * This fragment is for adding new cards to existing dictionary. It is used with
 * conjunction with {@link AddCardActivity}.
 */
public class AddCardFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    // Uri pointing to dictionary where to put new cards to.
    private Uri dictionary;

    // Id of the specified dictionary.
    private long dictionaryId;

    // UI elements.
    private TextView tvTitle;
    private EditText etFront;
    private EditText etBack;
    private Button btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionary = getActivity().getIntent().getData();
        dictionaryId = Long.parseLong(dictionary.getLastPathSegment());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_card_fragment, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        if (null == savedInstanceState) {
            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(CardsContract.Card.MAX_FRONT_LEN);
            etFront.setFilters(filters);

            filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(CardsContract.Card.MAX_BACK_LEN);
            etBack.setFilters(filters);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null)
            getLoaderManager().initLoader(DictionaryQuery._TOKEN, null, this);
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

    // Interface holding data about loader which
    // queries the title of the specified dictionary.
    private interface DictionaryQuery {
        // The identifier of loader which is passed to LoaderManager.
        int _TOKEN = 0;

        // Columns which we need.
        String[] COLUMNS = {
                CardsContract.Dictionary.DICTIONARY_TITLE
        };

        // Here should be the list of ids. These ids are used in Cursor to get
        // values from certain column of a row. Their names must be equal to
        // name of column without a table in order to easier remember what the
        // id each column corresponds to and to see in the code from which column
        // we are trying to get the value.
        int TITLE = 0;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Note: When the loader is the only we do not use switch on #id.
        // Because it is simpler to read.
        return new CursorLoader(getActivity(), dictionary, DictionaryQuery.COLUMNS, null, null, CardsContract.Dictionary.SORT_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        // Note: When the loader is the only we do not use switch on #loader.getId().
        // Because it is simpler to read.
        if (null == query)
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        query.moveToFirst();
        String title = query.getString(DictionaryQuery.TITLE);
        tvTitle.setText(title);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
