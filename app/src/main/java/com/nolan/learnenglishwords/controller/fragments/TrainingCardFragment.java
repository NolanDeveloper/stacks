package com.nolan.learnenglishwords.controller.fragments;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
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

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.model.CardsContract;

import java.util.Random;

public class TrainingCardFragment extends Fragment implements View.OnClickListener, LoaderCallbacks<Object> {
    private static final String EXTRA_DICTIONARY_TITLE = "dictionary.title";

    private TextView tvTitle;
    private TextView tvFront;
    private EditText etBack;
    private Button btnDone;

    private long cardPrevId;
    private long cardId;
    private String cardFront;
    private String cardBack;
    private int cardScrutiny;

    public static TrainingCardFragment GetInstance(@NonNull String dictionaryTitle) {
        TrainingCardFragment fragment = new TrainingCardFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_DICTIONARY_TITLE, dictionaryTitle);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(PickCardQuery.TOKEN, null, this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_card, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvFront = (TextView) view.findViewById(R.id.tv_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        tvTitle.setText(getArguments().getString(EXTRA_DICTIONARY_TITLE));
        return view;
    }

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        getLoaderManager().restartLoader(PickCardQuery.TOKEN, null, this).forceLoad();
        String userAssumption = etBack.getText().toString();
        etBack.getText().clear();
        final ContentResolver contentResolver = getActivity().getContentResolver();
        long dictionaryId = Long.parseLong(getActivity().getIntent().getData().getLastPathSegment());
        final Uri uri = ContentUris.withAppendedId(CardsContract.Card.buildUriToCardsOfDictionary(dictionaryId), cardId);
        final ContentValues values = new ContentValues();
        values.put(CardsContract.Card.CARD_SCRUTINY, cardScrutiny + (userAssumption.equals(cardBack) ? 1 : -1));
        new Thread(new Runnable() {
            @Override
            public void run() {
                contentResolver.update(uri, values, null, null);
            }
        }).run();
    }

    private interface PickCardQuery {
        int TOKEN = 0;

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

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        final long dictionaryId = Long.parseLong(getActivity().getIntent().getData().getLastPathSegment());
        final Uri cardsOfDictionary = CardsContract.Card.buildUriToCardsOfDictionary(dictionaryId);
        return new CursorLoader(getActivity(), cardsOfDictionary, PickCardQuery.COLUMNS, null, null, CardsContract.Card.SORT_LAST_SEEN);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        Cursor query = (Cursor) data;
        // todo: create complex distribution to be more
        // productive when we have tons of cards
        int count = query.getCount();
        cardPrevId = cardId;
        for (int i = 0; i < 10; i++) {
            int randRow = new Random().nextInt(count);
            query.moveToPosition(randRow);
            cardId = query.getLong(PickCardQuery.ID);
            if (cardId == cardPrevId)
                continue;
            cardFront = query.getString(PickCardQuery.FRONT);
            cardBack = query.getString(PickCardQuery.BACK);
            cardScrutiny = query.getInt(PickCardQuery.SCRUTINY);
            tvFront.setText(cardFront);
        }
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }
}
