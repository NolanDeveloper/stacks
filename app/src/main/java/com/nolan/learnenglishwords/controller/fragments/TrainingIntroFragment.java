package com.nolan.learnenglishwords.controller.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.model.CardsContract;

public class TrainingIntroFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Object> {

    public interface TrainingStarter {
        void startTraining(@NonNull String dictionaryTitle);
    }

    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnStart;
    private TrainingStarter activity;

    private boolean isDictionaryEmpty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (TrainingStarter) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_intro, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        btnStart = (Button) view.findViewById(R.id.btn_start);
        return view;
    }

    private interface DictionaryQuery {
        int TOKEN = 0;

        String[] COLUMNS = new String[] {
                CardsContract.Dictionary.DICTIONARY_TITLE,
                CardsContract.Dictionary.DICTIONARY_DESCRIPTION
        };

        int TITLE = 0;
        int DESCRIPTION = 1;
    }

    private interface CardQuery {
        int TOKEN = 1;

        String[] COLUMNS = new String[] {
                CardsContract.Card.CARD_ID
        };

        int ID = 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DictionaryQuery.TOKEN, null, this);
        getLoaderManager().initLoader(CardQuery.TOKEN, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DictionaryQuery.TOKEN:
                return new CursorLoader(getActivity(), getActivity().getIntent().getData(),
                        DictionaryQuery.COLUMNS, null, null, null);
            case CardQuery.TOKEN:
                String dictionaryId = getActivity().getIntent().getData().getLastPathSegment();
                Uri uri = CardsContract.Card.buildUriToCardsOfDictionary(Long.parseLong(dictionaryId));
                return new CursorLoader(getActivity(), uri, CardQuery.COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        Cursor query = (Cursor) data;
        switch (loader.getId()) {
            case DictionaryQuery.TOKEN:
                query.moveToFirst();
                String title = query.getString(DictionaryQuery.TITLE);
                String description = query.getString(DictionaryQuery.DESCRIPTION);
                tvTitle.setText(title);
                tvDescription.setText(description);
                break;
            case CardQuery.TOKEN:
                query.moveToFirst();
                isDictionaryEmpty = 0 == query.getCount();
                btnStart.setOnClickListener(this);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    @Override
    public void onClick(View v) {
        if (isDictionaryEmpty)
            // todo: replace it with offer to add words into dictionary
            Toast.makeText(getActivity(), getString(R.string.no_cards), Toast.LENGTH_SHORT).show();
        else
            activity.startTraining(tvTitle.getText().toString());
    }
}
