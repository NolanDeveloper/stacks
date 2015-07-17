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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nolane.stacks.provider.CardsContract.Cards;
import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment is for adding new cards to existing stack. It is used in
 * conjunction with {@link AddCardActivity}.
 */
public class AddCardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Uri which points to stack where to put new cards to.
    private Uri stack;

    // Id of the specified stack.
    private long stackId;

    // UI elements.
    @Bind(R.id.et_front)
    EditText etFront;
    @Bind(R.id.et_back)
    EditText etBack;
    @Bind(R.id.ib_bidirectional_help)
    ImageButton ibBidirectionalHelp;
    @Bind(R.id.cb_bidirectional)
    CheckBox cbBidirectional;
    @Bind(R.id.btn_done)
    Button btnDone;

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
        ButterKnife.bind(this, view);

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

    @OnClick(R.id.ib_bidirectional_help)
    public void showBidirectionalHelpDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.bidirectional)
                .setMessage(getString(R.string.bidirectional_help))
                .setNegativeButton(R.string.ok, null)
                .show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(StackQuery._TOKEN, null, this);
    }

    /**
     * Adds card into database according to the state of the views.
     */
    public void addCard() {
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
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });
        etBack.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addCard();
                return true;
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
