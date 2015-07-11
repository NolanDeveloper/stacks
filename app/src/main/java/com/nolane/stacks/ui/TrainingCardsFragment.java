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
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This fragment is used for training process. It is used in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingCardsFragment extends Fragment
        implements View.OnClickListener, LoaderCallbacks<Object> {
    /**
     * Interface for this class that allows to test it by using mock object that will
     * be able to control current time.
     */
    public interface Clock {
        /**
         * Must return the amount of milliseconds elapsed
         * since any constant point in the past.
         * @return The amount of milliseconds elapsed
         * since any constant point in the past.
         */
        long getCurrentTime();
    }

    // Strings corresponding to the values saved in onSaveInstanceState().
    private static final String EXTRA_CARD_ID = "card.id";
    private static final String EXTRA_CARD_FRONT = "card.front";
    private static final String EXTRA_CARD_BACK = "card.back";
    private static final String EXTRA_CARD_SCRUTINY = "card.scrutiny";
    private static final String EXTRA_CARD_LAST_SEEN = "card.last.seen";

    // UI elements.
    private TextView tvFront;
    private EditText etBack;
    private Button btnDone;

    // CardsContract.Card.CARD_ID value of showing card.
    private long cardId;
    // CardsContract.Card.CARD_BACK value of showing card.
    private String cardBack;
    // CardsContract.Card.CARD_SCRUTINY value of showing card.
    private int cardScrutiny;
    // CardsContract.Card.CARD_LAST_SEEN value of showing card.
    private long cardLastSeen;

    // The object which help to get current time.
    private Clock clock = new Clock() {
        @Override
        public long getCurrentTime() {
            return Calendar.getInstance().getTimeInMillis();
        }
    };

    /**
     * It's for testing purposes.
     * @param clock Object that represents current time.
     */
    public void setClock(@NonNull Clock clock) {
        this.clock = clock;
    }


    private Random random = new Random();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == savedInstanceState) {
            getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this).forceLoad();
        } else {
            cardId = savedInstanceState.getLong(EXTRA_CARD_ID);
            tvFront.setText(savedInstanceState.getString(EXTRA_CARD_FRONT));
            cardBack = savedInstanceState.getString(EXTRA_CARD_BACK);
            cardScrutiny = savedInstanceState.getInt(EXTRA_CARD_SCRUTINY);
            cardLastSeen = savedInstanceState.getLong(EXTRA_CARD_LAST_SEEN);
            btnDone.setOnClickListener(this);
            // Reconnect to started loaders.
            if (null != getLoaderManager().getLoader(PickCardQuery._TOKEN))
                getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this);
            if (null != getLoaderManager().getLoader(UpdateScrutinyQuery._TOKEN))
                getLoaderManager().initLoader(UpdateScrutinyQuery._TOKEN, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_CARD_ID, cardId);
        outState.putString(EXTRA_CARD_FRONT, tvFront.getText().toString());
        outState.putString(EXTRA_CARD_BACK, cardBack);
        outState.putInt(EXTRA_CARD_SCRUTINY, cardScrutiny);
        outState.putLong(EXTRA_CARD_SCRUTINY, cardLastSeen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_training_cards, container, false);
        tvFront = (TextView) view.findViewById(R.id.tv_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        return view;
    }

    private static final String VALUES = "values";

    @Override
    public void onClick(View v) {
        // Turn off button until we did not get next card.
        btnDone.setOnClickListener(null);
        long timeNow = clock.getCurrentTime();
        long timeDiff =  timeNow - cardLastSeen;
        if (TimeUnit.DAYS.toMillis(1) < timeDiff) {
            // Update scrutiny.
            String userAssumption = etBack.getText().toString();
            etBack.getText().clear();
            Bundle arguments = new Bundle();
            int newScrutiny = cardScrutiny + (userAssumption.equals(cardBack) ? 1 : -1);
            ContentValues values = new ContentValues();
            // todo: make preference for the bound of scrutiny
            if (getResources().getInteger(R.integer.default_min_scrutiny) <= newScrutiny)
                values.put(CardsContract.Card.CARD_SCRUTINY, newScrutiny);
            values.put(CardsContract.Card.CARD_LAST_SEEN, timeNow);
            arguments.putParcelable(VALUES, values);
            getLoaderManager().initLoader(UpdateScrutinyQuery._TOKEN, arguments, this).forceLoad();
        } else {
            getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this).forceLoad();
        }
    }

    private interface PickCardQuery {
        int _TOKEN = 0;

        String[] COLUMNS = new String[] {
                CardsContract.Card.CARD_ID,
                CardsContract.Card.CARD_FRONT,
                CardsContract.Card.CARD_BACK,
                CardsContract.Card.CARD_SCRUTINY
        };

        String SELECTION = CardsContract.Card.CARD_IN_LEARNING + " = 1";

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
        long stackId = Long.parseLong(getActivity().getIntent().getData().getLastPathSegment());
        switch (id) {
            case PickCardQuery._TOKEN: {
                // Here we use AsyncTaskLoader instead of CursorLoader because we don't need to have
                // observation on cursor. We just need to make one-shot load.
                final Uri cardsOfStack = CardsContract.Card.buildUriToCardsOfStack(stackId);
                return new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        return getActivity().getContentResolver().query(cardsOfStack, PickCardQuery.COLUMNS, PickCardQuery.SELECTION, null, CardsContract.Card.SORT_LAST_SEEN);
                    }
                };
            } case UpdateScrutinyQuery._TOKEN: {
                final Uri uri = ContentUris.withAppendedId(CardsContract.Card.buildUriToCardsOfStack(stackId), cardId);
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
        if (null == data) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        // All loader here are one-shot. So we need to
        // prevent them from saving previous results.
        getLoaderManager().destroyLoader(loader.getId());
        switch (loader.getId()) {
            case PickCardQuery._TOKEN:
                Cursor query = (Cursor) data;
                int count = query.getCount();
                if (0 == count) {
                    Toast.makeText(getActivity(), getString(R.string.all_done), Toast.LENGTH_LONG).show();
                    getFragmentManager().popBackStack();
                    return;
                }
                if (1 == count) {
                    query.moveToFirst();
                } else {
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
                getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this).forceLoad();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
