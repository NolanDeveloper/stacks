package com.nolan.learnenglishwords.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.model.Dictionary;

import java.sql.SQLException;

public class AddCardFragment extends Fragment implements View.OnClickListener {
    private class AddCardTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                BusinessLogic.GetInstance(getActivity()).addCard(dictionary.id, params[0], params[1]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getActivity(), getString(R.string.added), Toast.LENGTH_SHORT).show();
        }
    }
    public static final String ARG_DICTIONARY = "dictionary";

    private Dictionary dictionary;

    private TextView tvTitle;
    private EditText etFront;
    private EditText etBack;
    private Button btnDone;

    public static AddCardFragment GetInstance(@NonNull Dictionary dictionary) {
        AddCardFragment fragment = new AddCardFragment();
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
        View view = inflater.inflate(R.layout.add_card_fragment, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        tvTitle.setText(dictionary.title);
        btnDone.setOnClickListener(this);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(BusinessLogic.MAX_FRONT_LEN);
        etFront.setFilters(filters);
        filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(BusinessLogic.MAX_BACK_LEN);
        etBack.setFilters(filters);
        return view;
    }

    @Override
    public void onClick(View v) {
        String front = etFront.getText().toString();
        String back = etBack.getText().toString();
        etFront.getText().clear();
        etBack.getText().clear();
        new AddCardTask().execute(front, back);
    }
}
