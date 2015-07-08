package com.nolane.stacks.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This fragment is used to show preview of training. Preview is stack title and description.
 * In future we are going to add more useful information here. It is user in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingIntroFragment extends Fragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    // Interface that Activity must implement.
    public interface TrainingStarter {
        void startTraining();
    }

    // UI elements.
    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnStart;
    private TrainingStarter activity;

    // Flag showing if the specified stack if empty.
    private boolean isStackEmpty;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (TrainingStarter) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_training_intro, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        btnStart = (Button) view.findViewById(R.id.btn_start);
        return view;
    }

    /**
     * Query for stack title and description.
     */
    private interface StackQuery {
        int _TOKEN = 0;

        String[] COLUMNS = new String[] {
                CardsContract.Stacks.STACK_TITLE,
                CardsContract.Stacks.STACK_DESCRIPTION
        };

        int TITLE = 0;
        int DESCRIPTION = 1;
    }

    /**
     * Query for card ids to see if stack is empty.
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
        getLoaderManager().initLoader(StackQuery._TOKEN, null, this);
        getLoaderManager().initLoader(CardQuery._TOKEN, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case StackQuery._TOKEN:
                return new CursorLoader(getActivity(), getActivity().getIntent().getData(),
                        StackQuery.COLUMNS, null, null, null);
            case CardQuery._TOKEN:
                String stackId = getActivity().getIntent().getData().getLastPathSegment();
                Uri uri = CardsContract.Card.buildUriToCardsOfStack(Long.parseLong(stackId));
                return new CursorLoader(getActivity(), uri, CardQuery.COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        switch (loader.getId()) {
            case StackQuery._TOKEN:
                query.moveToFirst();
                String title = query.getString(StackQuery.TITLE);
                String description = query.getString(StackQuery.DESCRIPTION);
                tvTitle.setText(title);
                tvDescription.setText(description);
                UriUtils.insertParameter(getActivity(), CardsContract.Stacks.STACK_TITLE, tvTitle.getText().toString());
                break;
            case CardQuery._TOKEN:
                query.moveToFirst();
                isStackEmpty = 0 == query.getCount();
                btnStart.setOnClickListener(this);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        if (isStackEmpty) {
            // todo: replace it with offer to add words into stack
            Toast.makeText(getActivity(), getString(R.string.no_cards), Toast.LENGTH_SHORT).show();
        } else {
            activity.startTraining();
        }
    }
}
