package com.nolane.stacks.controller.fragments;

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

import com.nolane.stacks.R;
import com.nolane.stacks.controller.activities.TrainingActivity;
import com.nolane.stacks.model.CardsContract;

/**
 * This fragment is used to show preview of training. Preview is dictionary title and description.
 * In future we are going to add more useful information here. It is user in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingIntroFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    // Interface that Activity must implement.
    public interface TrainingStarter {
        void startTraining(@NonNull String dictionaryTitle);
    }

    // UI elements.
    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnStart;
    private TrainingStarter activity;

    // Flag showing if the specified dictionary if empty.
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

    /**
     * Query for dictionary title and description.
     */
    private interface DictionaryQuery {
        int _TOKEN = 0;

        String[] COLUMNS = new String[] {
                CardsContract.Dictionary.DICTIONARY_TITLE,
                CardsContract.Dictionary.DICTIONARY_DESCRIPTION
        };

        int TITLE = 0;
        int DESCRIPTION = 1;
    }

    /**
     * Query for card ids to see if dictionary is empty.
     */
    private interface CardQuery {
        int _TOKEN = 1;

        String[] COLUMNS = new String[] {
                CardsContract.Card.CARD_ID
        };

        int ID = 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DictionaryQuery._TOKEN, null, this);
        getLoaderManager().initLoader(CardQuery._TOKEN, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DictionaryQuery._TOKEN:
                return new CursorLoader(getActivity(), getActivity().getIntent().getData(),
                        DictionaryQuery.COLUMNS, null, null, null);
            case CardQuery._TOKEN:
                String dictionaryId = getActivity().getIntent().getData().getLastPathSegment();
                Uri uri = CardsContract.Card.buildUriToCardsOfDictionary(Long.parseLong(dictionaryId));
                return new CursorLoader(getActivity(), uri, CardQuery.COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query)
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        switch (loader.getId()) {
            case DictionaryQuery._TOKEN:
                query.moveToFirst();
                String title = query.getString(DictionaryQuery.TITLE);
                String description = query.getString(DictionaryQuery.DESCRIPTION);
                tvTitle.setText(title);
                tvDescription.setText(description);
                break;
            case CardQuery._TOKEN:
                query.moveToFirst();
                isDictionaryEmpty = 0 == query.getCount();
                btnStart.setOnClickListener(this);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
