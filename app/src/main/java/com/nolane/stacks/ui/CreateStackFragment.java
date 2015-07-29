package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.Stack;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment is for creating new stacks.
 * <p/>
 * {@link CreateStackActivity} and {@link CreateFirstStackActivity} use this.
 */
public class CreateStackFragment extends Fragment {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_create_stack, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.create_stack));

        int minMaxInLearning = getResources().getInteger(R.integer.min_max_in_learning);
        int maxMaxInLearning = getResources().getInteger(R.integer.max_max_in_learning);

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

        sbMaxInLearning.setMin(minMaxInLearning);
        sbMaxInLearning.setMax(maxMaxInLearning);
        int defaultMaxInLearning = getResources().getInteger(R.integer.default_max_in_learning);
        sbMaxInLearning.setProgress(defaultMaxInLearning);
        color = ((ColorDrawable) ibPickColor.getDrawable()).getColor();

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

    private void startAllStacksActivity() {
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

    /**
     * Performs creation of the stack according to the views on the screen.
     */
    private void createStack() {
        btnDone.setOnClickListener(null);
        String title = etTitle.getText().toString();
        String language = etLanguage.getText().toString();
        int maxInLearning = sbMaxInLearning.getProgress();
        int color = ((ColorDrawable) ibPickColor.getDrawable()).getColor();
        CardsDAO.getInstance()
                .createStack(title, language, maxInLearning, color)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Stack>() {
                    @Override
                    public void call(Stack stack) {
                        startAllStacksActivity();
                    }
                });
    }
}