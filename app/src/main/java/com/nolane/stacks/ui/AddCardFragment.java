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
import android.support.annotation.NonNull;
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
import com.nolane.stacks.utils.PreferencesUtils;

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
    // Id of the specified stack.
    private long stackId;
    private int stackCountInLearning;
    private int stackMaxInLearning;
    private int stackCountCards;

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
        stackId = getActivity().getIntent().getLongExtra(Stacks.STACK_ID, -1);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class InsertCardRunnable implements Runnable {
        private ContentResolver resolver;
        private String front;
        private String back;

        public InsertCardRunnable(@NonNull ContentResolver contentResolver,
                                  @NonNull String front, @NonNull String back) {
            this.resolver = contentResolver;
            this.front = front;
            this.back = back;
        }

        @Override
        public void run() {
            ContentValues values = new ContentValues();
            values.put(Cards.CARD_FRONT, front);
            values.put(Cards.CARD_BACK, back);
            values.put(Cards.CARD_STACK_ID, stackId);
            Uri data = Stacks.uriToStack(stackId);
            boolean inLearning = stackCountInLearning < stackMaxInLearning;
            values.put(Cards.CARD_IN_LEARNING, inLearning);
            resolver.insert(Cards.CONTENT_URI, values);

            values.clear();
            stackCountCards += 1;
            values.put(Stacks.STACK_COUNT_CARDS, stackCountCards);
            if (inLearning) {
                stackCountInLearning += 1;
                values.put(Stacks.STACK_COUNT_IN_LEARNING, stackCountInLearning + 1);
            }
            resolver.update(data, values, null, null);
        }
    }

    /**
     * Adds card into database according to the state of the views.
     */
    public void addCard() {
        final String front = etFront.getText().toString();
        final String back = etBack.getText().toString();
        final ContentResolver resolver = getActivity().getContentResolver();
        PreferencesUtils.cardWasAdded(getActivity());
        if (!cbBidirectional.isChecked()) {
            new Thread(new InsertCardRunnable(resolver, front, back)).run();
        } else {
            PreferencesUtils.cardWasAdded(getActivity());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new InsertCardRunnable(resolver, front, back).run();
                    new InsertCardRunnable(resolver, back, front).run();
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
                Stacks.STACK_MAX_IN_LEARNING,
                Stacks.STACK_COUNT_IN_LEARNING,
                Stacks.STACK_COUNT_CARDS
        };

        // Here should be the list of ids. These ids are used in Cursor to get
        // values from certain column of a row. Their names must be equal to
        // name of column without a table in order to easier remember what the
        // id each column corresponds to and to see in the code from which column
        // we are trying to get the value.
        int MAX_IN_LEARNING = 0;
        int COUNT_IN_LEARNING = 1;
        int COUNT_CARDS = 2;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Note: When the loader is the only we do not use switch on #id.
        // Because it is simpler to read.
        return new CursorLoader(getActivity(), Stacks.uriToStack(stackId), StackQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        // Note: When the loader is the only we do not use switch on #loader.getId().
        // Because it is simpler to read.
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        query.moveToFirst();
        stackCountCards = query.getInt(StackQuery.COUNT_CARDS);
        stackMaxInLearning = query.getInt(StackQuery.MAX_IN_LEARNING);
        stackCountInLearning = query.getInt(StackQuery.COUNT_IN_LEARNING);
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
