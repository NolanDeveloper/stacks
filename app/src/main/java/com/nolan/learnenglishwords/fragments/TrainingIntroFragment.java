package com.nolan.learnenglishwords.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.activities.TrainingActivity;
import com.nolan.learnenglishwords.model.Dictionary;

public class TrainingIntroFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_DICTIONARY = "dictionary";

    private Dictionary dictionary;

    private TextView tvTitle;
    private TextView tvDescription;
    private Button btnStart;
    private TrainingActivity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (TrainingActivity) activity;
    }

    public static TrainingIntroFragment GetInstance(@NonNull Dictionary dictionary) {
        TrainingIntroFragment fragment = new TrainingIntroFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_DICTIONARY, dictionary);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionary = (Dictionary) getArguments().getSerializable(ARG_DICTIONARY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.training_intro, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);
        btnStart = (Button) view.findViewById(R.id.btn_start);

        tvTitle.setText(dictionary.title);
        tvDescription.setText(dictionary.description);
        btnStart.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        activity.startTraining();
    }
}
