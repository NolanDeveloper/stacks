package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.Cards;
import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment is for adding new cards to existing stack. It is used in
 * conjunction with {@link AddCardActivity}. <br>
 * Required: <br>
 * data type: {@link Stacks#CONTENT_ITEM_TYPE}
 */
public class AddCardFragment extends Fragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>, TextView.OnEditorActionListener {
    // Uri which points to stack where to put new cards to.
    private Uri stack;

    // Id of the specified stack.
    private long stackId;

    // UI elements.
    private EditText etFront;
    private EditText etBack;
    private ImageButton ibBidirectionalHelp;
    private CheckBox cbBidirectional;
    private Button btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stack = getActivity().getIntent().getData();
        stackId = Long.parseLong(stack.getLastPathSegment());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_add_card, container, false);
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        ibBidirectionalHelp = (ImageButton) view.findViewById(R.id.ib_bidirectional_help);
        cbBidirectional = (CheckBox) view.findViewById(R.id.cb_bidirectional);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        etBack.setOnEditorActionListener(this);
        ibBidirectionalHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.bidirectional)
                        .setMessage(getString(R.string.bidirectional_help))
                        .setNegativeButton(android.R.string.ok, null)
                        .show();
            }
        });
        getActivity().setTitle(getString(R.string.add_card));

        if (null == savedInstanceState) {
            etFront.setText("");

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(Cards.MAX_FRONT_LEN);
            etFront.setFilters(filters);

            filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(Cards.MAX_BACK_LEN);
            etBack.setFilters(filters);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UriUtils.checkDataTypeOrThrow(getActivity(), Stacks.CONTENT_ITEM_TYPE);
        getLoaderManager().initLoader(StackQuery._TOKEN, null, this);
    }

    @Override
    public void onClick(View v) {
        String front = etFront.getText().toString();
        String back = etBack.getText().toString();
        final ContentResolver resolver = getActivity().getContentResolver();
        if (!cbBidirectional.isChecked()) {
            final ContentValues values = new ContentValues();
            values.put(Cards.CARD_FRONT, front);
            values.put(Cards.CARD_BACK, back);
            values.put(Cards.CARD_STACK_ID, stackId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resolver.insert(Cards.CONTENT_URI, values);
                }
            }).run();
        } else {
            final ContentValues valuesOne = new ContentValues();
            valuesOne.put(Cards.CARD_FRONT, front);
            valuesOne.put(Cards.CARD_BACK, back);
            valuesOne.put(Cards.CARD_STACK_ID, stackId);
            final ContentValues valuesTwo = new ContentValues();
            valuesTwo.put(Cards.CARD_FRONT, back);
            valuesTwo.put(Cards.CARD_BACK, front);
            valuesTwo.put(Cards.CARD_STACK_ID, stackId);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    resolver.insert(Cards.CONTENT_URI, valuesOne);
                    resolver.insert(Cards.CONTENT_URI, valuesTwo);
                }
            }).run();
        }
        etFront.getText().clear();
        etBack.getText().clear();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(null);
        return true;
    }

    /**
     * Interface holding data about loader which
     * queries the title of the specified stack.
     */
    private interface StackQuery {
        // The identifier of loader which is passed to LoaderManager.
        int _TOKEN = 0;

        // Columns which we need.
        String[] COLUMNS = {
                Stacks.STACK_TITLE
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
        return new CursorLoader(getActivity(), stack, StackQuery.COLUMNS, null, null, Stacks.SORT_DEFAULT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        // Note: When the loader is the only we do not use switch on #loader.getId().
        // Because it is simpler to read.
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        query.moveToFirst();
        String title = query.getString(StackQuery.TITLE);
        UriUtils.insertParameter(getActivity(), Stacks.STACK_TITLE, title);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
