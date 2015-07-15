package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
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
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment allows user to edit stack. <br>
 * Required: <br>
 * data type: {@link Stacks#CONTENT_ITEM_TYPE} <br>
 * data parameter: {@link Stacks#STACK_TITLE} <br>
 * data parameter: {@link Stacks#STACK_LANGUAGE} <br>
 * data parameter: {@link Stacks#STACK_COLOR}
 */
public class EditStackFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    // Actual values of stack.
    private String title;
    private String language;
    private int color;

    // UI elements.
    private EditText etTitle;
    private EditText etLanguage;
    private ImageButton ibPickColor;
    private Button btnAddCards;
    private Button btnDone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_stack, container, false);
        etTitle = (EditText) view.findViewById(R.id.et_title);
        etLanguage = (EditText) view.findViewById(R.id.et_language);
        ibPickColor = (ImageButton) view.findViewById(R.id.ib_pick_color);
        btnAddCards = (Button) view.findViewById(R.id.btn_add_cards);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        btnAddCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCardActivity.class);
                intent.setData(getActivity().getIntent().getData());
                startActivity(intent);
                getActivity().finish();
            }
        });
        btnDone.setOnClickListener(this);
        etLanguage.setOnEditorActionListener(this);
        ibPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog(getActivity(), new ColorPickerDialog.OnPickedColorListener() {
                    @Override
                    public void onPickedColor(int color) {
                        ibPickColor.setImageDrawable(new ColorDrawable(color));
                    }
                }).show();
            }
        });
        title = getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_TITLE);
        language = getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_LANGUAGE);
        color = Integer.parseInt(getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_COLOR));
        ibPickColor.setImageDrawable(new ColorDrawable(color));
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
        final int newColor = ((ColorDrawable) ibPickColor.getDrawable()).getColor();
        if (!newTitle.equals(title) || !newLanguage.equals(language) || color != newColor) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = getActivity().getIntent().getData();
                    ContentValues values = new ContentValues();
                    values.put(Stacks.STACK_TITLE, newTitle);
                    values.put(Stacks.STACK_LANGUAGE, newLanguage);
                    values.put(Stacks.STACK_COLOR, newColor);
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
        UriUtils.checkSpecifiesParameterOrThrow(getActivity(), Stacks.STACK_COLOR);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(btnDone);
        return true;
    }
}
