package com.nolane.stacks.ui;

import android.app.Dialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nolane.stacks.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import static com.nolane.stacks.provider.CardsContract.*;

/**
 * This fragment is for creating new stacks.
 * <p>
 * {@link CreateStackActivity} and {@link CreateFirstStackActivity} use this.
 */
public class CreateStackFragment extends Fragment
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Uri>, TextView.OnEditorActionListener {
    // Save instance state keys.
    private static final String EXTRA_COLOR = "color";

    // UI elements.
    private EditText etTitle;
    private EditText etLanguage;
    private ImageButton ibPickColor;
    private Button btnDone;
    private DiscreteSeekBar sbMaxInLearning;

    // Color that user picked.
    private int color;

    // Limits of max in learning cards.
    private int minMaxInLearning;
    private int maxMaxInLearning;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_create_stack, container, false);

        etTitle = (EditText) view.findViewById(R.id.et_title);
        etLanguage = (EditText) view.findViewById(R.id.et_language);
        ibPickColor = (ImageButton) view.findViewById(R.id.ib_pick_color);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        sbMaxInLearning = (DiscreteSeekBar) view.findViewById(R.id.sb_max_in_learning);

        minMaxInLearning = getResources().getInteger(R.integer.min_max_in_learning);
        maxMaxInLearning = getResources().getInteger(R.integer.max_max_in_learning);

        btnDone.setOnClickListener(this);
        etTitle.setOnEditorActionListener(this);
        ibPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog(getActivity(), new ColorPickerDialog.OnPickedColorListener() {
                    @Override
                    public void onPickedColor(int color) {
                        CreateStackFragment.this.color = color;
                        ibPickColor.setImageDrawable(new ColorDrawable(color));
                    }
                }).show();
            }
        });

        if (null == savedInstanceState) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(Stacks.MAX_TITLE_LEN);
            etTitle.setFilters(filterArray);
            etTitle.setText(null);

            filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(Stacks.MAX_LANGUAGE_LEN);
            etLanguage.setFilters(filterArray);

            sbMaxInLearning.setMin(minMaxInLearning);
            sbMaxInLearning.setMax(maxMaxInLearning);
            int defaultMaxInLearning = getResources().getInteger(R.integer.default_max_in_learning);
            sbMaxInLearning.setProgress(defaultMaxInLearning);

            color = ((ColorDrawable) ibPickColor.getDrawable()).getColor();
        } else {
            color = savedInstanceState.getInt(EXTRA_COLOR);
            ibPickColor.setImageDrawable(new ColorDrawable(color));
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_COLOR, color);
    }

    // Key to store ContentValues in Bundle.
    private static final String EXTRA_VALUES = "values";

    @Override
    public void onClick(View v) {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String language = etLanguage.getText().toString();
        int maxInLearning = sbMaxInLearning.getProgress();
        // If you know better way to pass values though Bundle please,
        // make pull request on https://github.com/Nolane/learn-english-words
        Bundle args = new Bundle();
        ContentValues values = new ContentValues();
        values.put(Stacks.STACK_TITLE, title);
        values.put(Stacks.STACK_LANGUAGE, language);
        values.put(Stacks.STACK_MAX_IN_LEARNING, maxInLearning);
        values.put(Stacks.STACK_COLOR, color);
        args.putParcelable(EXTRA_VALUES, values);
        getLoaderManager().initLoader(0, args, this).forceLoad();
    }

    @Override
    public Loader<Uri> onCreateLoader(int id, Bundle args) {
        // Here we use AsyncTaskLoader instead of simple AsyncTask because we need
        // to start another activity after inserting even if this activity was recreated during
        // inserting.
        final ContentValues values = args.getParcelable(EXTRA_VALUES);
        return new AsyncTaskLoader<Uri>(getActivity()) {
            @Override
            public Uri loadInBackground() {
                return getContext().getContentResolver().insert(Stacks.CONTENT_URI, values);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Uri> loader, Uri uri) {
        if (null == uri) {
            throw new IllegalArgumentException("Loader was failed. (uri = null)");
        }
        Intent intent = new Intent(getActivity(), AllStacksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(btnDone);
        return true;
    }
}
