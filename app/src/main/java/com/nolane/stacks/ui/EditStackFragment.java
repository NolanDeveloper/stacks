package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CardsDatabase.Tables;
import com.nolane.stacks.provider.Stack;
import com.nolane.stacks.utils.BaseTextWatcher;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.schedulers.Schedulers;

/**
 * This fragment allows user to edit stack.
 */
public class EditStackFragment extends Fragment {
    private Stack stack;

    // UI elements.
    @Bind(R.id.til_title)
    TextInputLayout tilTitle;
    @Bind(R.id.til_language)
    TextInputLayout tilLanguage;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        stack = (Stack) getActivity().getIntent().getSerializableExtra(Tables.STACKS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_stack, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.edit));
        etTitle.setText(stack.title);
        etLanguage.setText(stack.language);
        //noinspection ConstantConditions
        ibPickColor.setImageDrawable(new ColorDrawable(stack.color));

        etTitle.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Stack.checkTitle(s)) {
                    etTitle.setError(getString(R.string.too_long));
                }
            }
        });
        etLanguage.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Stack.checkLanguage(s)) {
                    etLanguage.setError(getString(R.string.too_long));
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_done)
    public void done(View v) {
        final String newTitle = etTitle.getText().toString();
        final String newLanguage = etLanguage.getText().toString();
        final int newColor = ((ColorDrawable) ibPickColor.getDrawable()).getColor();
        if (!Stack.checkTitle(newTitle) || !Stack.checkLanguage(newLanguage)) {
            return;
        }
        //noinspection ConstantConditions
        if (!newTitle.equals(stack.title) || !newLanguage.equals(stack.language) || stack.color != newColor) {
            CardsDAO.getInstance()
                    .changeStack(stack.id, newTitle, newLanguage, newColor)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        getActivity().finish();
    }

    @OnEditorAction(R.id.et_language)
    public boolean done() {
        done(btnDone);
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
                        CardsDAO.getInstance()
                                .deleteStack(stack.id)
                                .subscribeOn(Schedulers.io())
                                .subscribe();
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
