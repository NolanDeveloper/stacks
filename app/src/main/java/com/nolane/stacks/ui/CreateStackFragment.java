package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;

/**
 * This fragment is for creating new stacks.
 * <p>
 * {@link CreateStackActivity} and {@link CreateFirstStackActivity} use this.
 */
public class CreateStackFragment extends Fragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Uri> {
    // UI elements.
    private EditText etTitle;
    private EditText etDescription;
    private Button btnDone;
    private TextView tvMin;
    private TextView tvMax;
    private SeekBar sbMaxInLearning;

    // Limits of max in learning cards.
    private int minMaxInLearning;
    private int maxMaxInLearning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_create_stack, container, false);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etDescription = (EditText) view.findViewById(R.id.et_description);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        tvMin = (TextView) view.findViewById(R.id.tv_min);
        tvMax = (TextView) view.findViewById(R.id.tv_max);
        sbMaxInLearning = (SeekBar) view.findViewById(R.id.sb_max_in_learning);

        minMaxInLearning = getResources().getInteger(R.integer.min_max_in_learning);
        maxMaxInLearning = getResources().getInteger(R.integer.max_max_in_learning);

        btnDone.setOnClickListener(this);

        if (null == savedInstanceState) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(CardsContract.Stacks.MAX_TITLE_LEN);
            etTitle.setFilters(filterArray);

            filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(CardsContract.Stacks.MAX_DESCRIPTION_LEN);
            etDescription.setFilters(filterArray);

            tvMin.setText(String.valueOf(minMaxInLearning));
            tvMax.setText(String.valueOf(maxMaxInLearning));
            sbMaxInLearning.setMax(maxMaxInLearning - minMaxInLearning);
            int defaultMaxInLearning = getResources().getInteger(R.integer.default_max_in_learning);
            sbMaxInLearning.setProgress(defaultMaxInLearning - minMaxInLearning);
        }
        return view;
    }

    private static final String VALUES = "values";

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        int maxInLearning = sbMaxInLearning.getProgress() + minMaxInLearning;
        // If you know better way to pass values though Bundle please,
        // make pull request on https://github.com/Nolane/learn-english-words
        Bundle args = new Bundle();
        ContentValues values = new ContentValues();
        values.put(CardsContract.Stacks.STACK_TITLE, title);
        values.put(CardsContract.Stacks.STACK_DESCRIPTION, description);
        values.put(CardsContract.Stacks.STACK_MAX_IN_LEARNING, maxInLearning);
        args.putParcelable(VALUES, values);
        getLoaderManager().initLoader(0, args, this).forceLoad();
    }

    @Override
    public Loader<Uri> onCreateLoader(int id, Bundle args) {
        // Here we use AsyncTaskLoader instead of simple AsyncTask because we need
        // to start another activity after inserting even if this activity was recreated during
        // inserting.
        final ContentValues values = args.getParcelable(VALUES);
        return new AsyncTaskLoader<Uri>(getActivity()) {
            @Override
            public Uri loadInBackground() {
                return getContext().getContentResolver().insert(CardsContract.Stacks.CONTENT_URI, values);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Uri> loader, Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("Loader was failed. (uri = null)");
        }
        Intent intent = new Intent(getActivity().getBaseContext(), AddCardActivity.class);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {

    }
}
