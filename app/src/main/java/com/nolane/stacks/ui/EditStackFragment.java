package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.ContentValues;
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
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.*;

/**
 * This fragment allows user to edit stack. <br>
 * Required: <br>
 * data type: {@link Stacks#CONTENT_ITEM_TYPE} <br>
 * data parameter: {@link Stacks#STACK_TITLE} <br>
 * data parameter: {@link Stacks#STACK_LANGUAGE}
 */
public class EditStackFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    // Actual values of stack.
    private String title;
    private String language;

    // UI elements.
    private EditText etTitle;
    private EditText etLanguage;
    private Button btnDone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_stack, container, false);
        etTitle = (EditText) view.findViewById(R.id.et_title);
        etLanguage = (EditText) view.findViewById(R.id.et_language);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);
        etLanguage.setOnEditorActionListener(this);
        title = getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_TITLE);
        language = getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_LANGUAGE);
        if (null == savedInstanceState) {
            etTitle.setText(title);
            etLanguage.setText(language);

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(Stacks.MAX_TITLE_LEN);
            etTitle.setFilters(filterArray);

            filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(Stacks.MAX_LANGUAGE_LEN);
            etLanguage.setFilters(filterArray);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        final String newTitle = etTitle.getText().toString();
        final String newLanguage = etLanguage.getText().toString();
        if (!newTitle.equals(title) || !newLanguage.equals(language)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = getActivity().getIntent().getData();
                    ContentValues values = new ContentValues();
                    values.put(Stacks.STACK_TITLE, newTitle);
                    values.put(Stacks.STACK_LANGUAGE, newLanguage);
                    getActivity().getContentResolver().update(uri, values, null, null);
                }
            }).run();
        }
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UriUtils.checkDataTypeOrThrow(getActivity(), Stacks.CONTENT_ITEM_TYPE);
        UriUtils.checkSpecifiesParameterOrThrow(getActivity(), Stacks.STACK_TITLE);
        UriUtils.checkSpecifiesParameterOrThrow(getActivity(), Stacks.STACK_LANGUAGE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(btnDone);
        return true;
    }
}
