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
import com.nolane.stacks.utils.ColorUtils;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.*;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * This fragment is used for training process. It is used in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingFragment extends Fragment
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
    private static final String EXTRA_CARD_PROGRESS = "card.progress";
    private static final String EXTRA_CARD_LAST_SEEN = "card.last.seen";

    // Key which is used to pass ContentValues though Bundle.
    private static final String EXTRA_VALUES = "values";

    // UI elements.
    private View vProgressIndicator;
    private TextView tvFront;
    private EditText etBack;
    private Button btnDone;

    // Card.CARD_ID value of showing card.
    private long cardId;
    // Card.CARD_BACK value of showing card.
    private String cardBack;
    // Card.CARD_PROGRESS value of showing card.
    private int cardProgress;
    // Card.CARD_LAST_SEEN value of showing card.
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
            UriUtils.checkDataTypeOrThrow(getActivity(), Stacks.CONTENT_ITEM_TYPE);
        } else {
            cardId = savedInstanceState.getLong(EXTRA_CARD_ID);
            tvFront.setText(savedInstanceState.getString(EXTRA_CARD_FRONT));
            cardBack = savedInstanceState.getString(EXTRA_CARD_BACK);
            cardProgress = savedInstanceState.getInt(EXTRA_CARD_PROGRESS);
            cardLastSeen = savedInstanceState.getLong(EXTRA_CARD_LAST_SEEN);
            btnDone.setOnClickListener(this);
            // Reconnect to started loaders.
            if (null != getLoaderManager().getLoader(PickCardQuery._TOKEN))
                getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this);
            if (null != getLoaderManager().getLoader(UpdateProgressQuery._TOKEN))
                getLoaderManager().initLoader(UpdateProgressQuery._TOKEN, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_CARD_ID, cardId);
        outState.putString(EXTRA_CARD_FRONT, tvFront.getText().toString());
        outState.putString(EXTRA_CARD_BACK, cardBack);
        outState.putInt(EXTRA_CARD_PROGRESS, cardProgress);
        outState.putLong(EXTRA_CARD_PROGRESS, cardLastSeen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_training, container, false);
        vProgressIndicator = view.findViewById(R.id.v_progress_indicator);
        tvFront = (TextView) view.findViewById(R.id.tv_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        return view;
    }

    @Override
    public void onClick(View v) {
        // Turn off button until we did not get next card.
        btnDone.setOnClickListener(null);
        long timeNow = clock.getCurrentTime();
        long timeDiff =  timeNow - cardLastSeen;
        long dayInMills = TimeUnit.DAYS.toMillis(1);
        String userAssumption = etBack.getText().toString();
        etBack.getText().clear();
        if (dayInMills < timeDiff) {
            // Update progress.
            Bundle arguments = new Bundle();
            int newProgress = cardProgress + (userAssumption.equals(cardBack) ? 1 : -1);
            ContentValues values = new ContentValues();
            // todo: make preference for the bound of progress
            if (getResources().getInteger(R.integer.default_min_progress) <= newProgress)
                values.put(Cards.CARD_PROGRESS, newProgress);
            values.put(Cards.CARD_LAST_SEEN, timeNow);
            arguments.putParcelable(EXTRA_VALUES, values);
            getLoaderManager().initLoader(UpdateProgressQuery._TOKEN, arguments, this).forceLoad();
        } else {
            getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this).forceLoad();
        }
    }

    private interface PickCardQuery {
        int _TOKEN = 0;

        String[] COLUMNS = new String[] {
                Cards.CARD_ID,
                Cards.CARD_FRONT,
                Cards.CARD_BACK,
                Cards.CARD_PROGRESS,
                Cards.CARD_LAST_SEEN
        };

        String SELECTION = Cards.CARD_IN_LEARNING + " = 1";

        int ID = 0;
        int FRONT = 1;
        int BACK = 2;
        int PROGRESS = 3;
        int LAST_SEEN = 4;
    }

    private interface UpdateProgressQuery {
        int _TOKEN = 1;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        long stackId = Long.parseLong(getActivity().getIntent().getData().getLastPathSegment());
        switch (id) {
            case PickCardQuery._TOKEN: {
                // Here we use AsyncTaskLoader instead of CursorLoader because we don't need to have
                // observation on cursor. We just need to make one-shot load.
                final Uri cardsOfStack = Cards.buildUriToCardsOfStack(stackId);
                return new AsyncTaskLoader(getActivity()) {
                    @Override
                    public Object loadInBackground() {
                        return getActivity().getContentResolver().query(cardsOfStack, PickCardQuery.COLUMNS, PickCardQuery.SELECTION, null, Cards.SORT_LAST_SEEN);
                    }
                };
            } case UpdateProgressQuery._TOKEN: {
                final Uri uri = ContentUris.withAppendedId(Cards.buildUriToCardsOfStack(stackId), cardId);
                final ContentValues values = args.getParcelable(EXTRA_VALUES);
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
                    // fixme: notify user that we run out of cards.
//                    Snackbar.make(getView(), getString(R.string.all_done), Snackbar.LENGTH_SHORT)
//                            .show();
                    getActivity().finish();
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
                cardProgress = query.getInt(PickCardQuery.PROGRESS);
                cardLastSeen = query.getLong(PickCardQuery.LAST_SEEN);
                vProgressIndicator.setBackgroundColor(ColorUtils.getColorForProgress(getActivity(), cardProgress));
                String cardFront = query.getString(PickCardQuery.FRONT);
                tvFront.setText(cardFront);
                // Do not forget to turn button on.
                btnDone.setOnClickListener(this);
                break;
            case UpdateProgressQuery._TOKEN:
                getLoaderManager().initLoader(PickCardQuery._TOKEN, null, this).forceLoad();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
