package com.nolane.stacks.ui;

import android.app.AlertDialog;
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
import android.widget.TextView;

import com.nolane.stacks.R;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment is for creating new stacks.
 * <p/>
 * {@link CreateStackActivity} and {@link CreateFirstStackActivity} use this.
 */
public class CreateStackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Uri> {
    // Key to store ContentValues in Bundle.
    private static final String EXTRA_VALUES = "values";
    // Save instance state keys.
    private static final String EXTRA_COLOR = "color";

    // UI elements.
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_language)
    EditText etLanguage;
    @Bind(R.id.ib_speed_help)
    ImageButton ibSpeedHelp;
    @Bind(R.id.btn_done)
    Button btnDone;
    @Bind(R.id.ib_pick_color)
    ImageButton ibPickColor;
    @Bind(R.id.sb_max_in_learning)
    DiscreteSeekBar sbMaxInLearning;

    // Color that user picked.
    private int color;

    // Limits of max in learning cards.
    private int minMaxInLearning;
    private int maxMaxInLearning;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_COLOR, color);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_create_stack, container, false);
        ButterKnife.bind(this, view);

        minMaxInLearning = getResources().getInteger(R.integer.min_max_in_learning);
        maxMaxInLearning = getResources().getInteger(R.integer.max_max_in_learning);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStack();
            }
        });
        etLanguage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                createStack();
                return true;
            }
        });
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

    /**
     * Show help dialog that explains what parameter "speed" means.
     */
    @OnClick(R.id.ib_speed_help)
    void showSpeedHelp() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.speed)
                .setMessage(getString(R.string.speed_help))
                .setNegativeButton(R.string.ok, null)
                .show();
    }

    /**
     * Performs creation of the stack according to the views on the screen.
     */
    private void createStack() {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String language = etLanguage.getText().toString();
        int maxInLearning = sbMaxInLearning.getProgress();
        // If you know better way to pass values though nolane16@gmail.com
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
        // This frag is required because this fragment is used in two places.
        // First is CreateStackActivity and second is CreateFirstStackActivity.
        // Using this flag allows to keep code simpler. Scheme below shows problem.
        // We don't want to see two AllStacksActivity in back stack.
        /*
            LauncherActivity (finished to clear back stack)
                    |
            CreateFirstStackActivity (finished to clear back stack)
            <CreateStackFragment>
                    |
            AllStacksActivity (not finished)
                    |
            CreateStackActivity (finished to clear back stack)
            <CreateStackFragment>
                    |
            AllStacksActivity (not finished)
         */
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onLoaderReset(Loader<Uri> loader) {

    }
}