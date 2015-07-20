package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nolane.stacks.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment allows user to edit stack.
 */
public class EditStackFragment extends Fragment {
    // Actual values of stack.
    private String title;
    private String language;
    private int color;

    // UI elements.
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_language)
    EditText etLanguage;
    @Bind(R.id.ib_pick_color)
    ImageButton ibPickColor;
    @Bind(R.id.btn_remove)
    Button btnRemove;
    @Bind(R.id.btn_done)
    Button btnDone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_stack, container, false);
        ButterKnife.bind(this, view);
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

    @OnClick(R.id.btn_done)
    public void finishEditing(View v) {
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

    @OnEditorAction(R.id.et_language)
    public boolean finishEditing() {
        finishEditing(btnDone);
        return true;
    }

    @OnClick(R.id.btn_remove)
    public void deleteAfterConfirmation() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.remove)
                .setMessage(getString(R.string.ask_delete))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Context context = getActivity().getApplicationContext();
                        final Uri data = getActivity().getIntent().getData();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ContentValues values = new ContentValues();
                                values.put(Stacks.STACK_DELETED, true);
                                context.getContentResolver().update(data, values, null, null);
                            }
                        }).run();
                        getActivity().finish();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @OnClick(R.id.ib_pick_color)
    public void showPickColorDialog() {
        new ColorPickerDialog(getActivity(), new ColorPickerDialog.OnPickedColorListener() {
            @Override
            public void onPickedColor(int color) {
                ibPickColor.setImageDrawable(new ColorDrawable(color));
            }
        }).show();
    }
}
