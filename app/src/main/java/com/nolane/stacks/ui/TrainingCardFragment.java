package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;

import java.util.Random;

// todo: refactor this bullshit
// todo: or rewrite it at all

/**
 * This fragment is used for training process. It is user in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingCardFragment extends Fragment implements View.OnClickListener, LoaderCallbacks<Object> {
    // String which identifies dictionary title argument passing to this fragment.
    private static final String EXTRA_DICTIONARY_TITLE = "dictionary.title";
    // Strings corresponding to the values saved in onSaveInstanceState().
    private static final String EXTRA_CARD_ID = "card.id";
    private static final String EXTRA_CARD_FRONT = "card.front";
    private static final String EXTRA_CARD_BACK = "card.back";
    private static final String EXTRA_CARD_SCRUTINY = "card.scrutiny";

    // UI elements.
    private TextView tvTitle;
    private TextView tvFront;
    private EditText etBack;
    private Button btnDone;

    // CardsContract.Card.CARD_ID value of showing card.
    private long cardId;
    // CardsContract.Card.CARD_BACK value of showing card.
    private String cardBack;
    // CardsContract.Card.CARD_SCRUTINY value of showing card.
    private int cardScrutiny;

    private Random random = new Random();

    /**
     * Creates instance of this fragment passing correct arguments to it.
     * @param dictionaryTitle The title of the specified dictionary.
     * @return TrainingCardFragment with correct arguments.
     */
    public static TrainingCardFragment getInstance(@NonNull String dictionaryTitle) {
        TrainingCardFragment fragment = new TrainingCardFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_DICTIONARY_TITLE, dictionaryTitle);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != savedInstanceState) {
            cardId = savedInstanceState.getLong(EXTRA_CARD_ID);
            tvFront.setText(savedInstanceState.getString(EXTRA_CARD_FRONT));
            cardBack = savedInstanceState.getString(EXTRA_CARD_BACK);
            cardScrutiny = savedInstanceState.getInt(EXTRA_CARD_SCRUTINY);
            btnDone.setOnClickListener(this);
        } else {
            getLoaderManager().restartLoader(PickCardQuery._TOKEN, null, this).forceLoad();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_CARD_ID, cardId);
        outState.putString(EXTRA_CARD_FRONT, tvFront.getText().toString());
        outState.putString(EXTRA_CARD_BACK, cardBack);
        outState.putInt(EXTRA_CARD_SCRUTINY, cardScrutiny);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.training_card, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvFront = (TextView) view.findViewById(R.id.tv_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        tvTitle.setText(getArguments().getString(EXTRA_DICTIONARY_TITLE));
        return view;
    }

    private static final String VALUES = "values";

    @Override
    public void onClick(View v) {
        // Turn off button until we did not get next card.
        btnDone.setOnClickListener(null);
        // Update scrutiny
        // todo: fix wrong logic
        String userAssumption = etBack.getText().toString();
        etBack.getText().clear();
        Bundle arguments = new Bundle();
        ContentValues values = new ContentValues();
        values.put(CardsContract.Card.CARD_SCRUTINY, cardScrutiny + (userAssumption.equals(cardBack) ? 1 : -1));
        arguments.putParcelable(VALUES, values);
        getLoaderManager().restartLoader(UpdateScrutinyQuery._TOKEN, arguments, this).forceLoad();
    }

    private interface PickCardQuery {
        int _TOKEN = 0;

        String[] COLUMNS = new String[] {
                CardsContract.Card.CARD_ID,
                CardsContract.Card.CARD_FRONT,
                CardsContract.Card.CARD_BACK,
                CardsContract.Card.CARD_SCRUTINY
        };

        int ID = 0;
        int FRONT = 1;
        int BACK = 2;
        int SCRUTINY = 3;
    }

    private interface UpdateScrutinyQuery {
        int _TOKEN = 1;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        long dictionaryId = Long.parseLong(getActivity().getIntent().getData().getLastPathSegment());
        switch (id) {
            case PickCardQuery._TOKEN: {
                final Uri cardsOfDictionary = CardsContract.Card.buildUriToCardsOfDictionary(dictionaryId);
                return new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        return getActivity().getContentResolver().query(cardsOfDictionary, PickCardQuery.COLUMNS, null, null, CardsContract.Card.SORT_LAST_SEEN);
                    }
                };
            } case UpdateScrutinyQuery._TOKEN: {
                final Uri uri = ContentUris.withAppendedId(CardsContract.Card.buildUriToCardsOfDictionary(dictionaryId), cardId);
                final ContentValues values = args.getParcelable(VALUES);
                return new AsyncTaskLoader<Object>(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        return getActivity().getContentResolver().update(uri, values, null, null);
                    }
                };
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case PickCardQuery._TOKEN:
                Cursor query = (Cursor) data;
                if (null == query) {
                    throw new IllegalArgumentException("Loader was failed. (query = null)");
                }
                int count = query.getCount();
                if (0 == count) {
                    throw new IllegalArgumentException("The specified dictionary does not have cards.");
                }
                if (1 == count) {
                    query.moveToFirst();
                } else {
                    // todo: create complex distribution to be more
                    // todo: productive when we have tons of cards
                    long cardPrevId = cardId;
                    // If you get the same card 5 times in a row it means God wants you to see this card.
                    for (int i = 0; (cardId == cardPrevId) && (i < 5); i++) {
                        int position = random.nextInt(count);
                        query.moveToPosition(position);
                        cardId = query.getLong(PickCardQuery.ID);
                    }
                }
                cardId = query.getLong(PickCardQuery.ID);
                cardBack = query.getString(PickCardQuery.BACK);
                cardScrutiny = query.getInt(PickCardQuery.SCRUTINY);
                String cardFront = query.getString(PickCardQuery.FRONT);
                tvFront.setText(cardFront);
                // Do not forget to turn button on.
                btnDone.setOnClickListener(this);
                break;
            case UpdateScrutinyQuery._TOKEN:
                getLoaderManager().restartLoader(PickCardQuery._TOKEN, null, this).forceLoad();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
