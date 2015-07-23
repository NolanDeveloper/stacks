package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
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
import com.nolane.stacks.utils.PreferencesUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This fragment allows user to edit stack.
 */
public class EditStackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // Id of the stack that we edit.
    private long stackId;

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
        getActivity().setTitle(getString(R.string.edit));
        if (null == savedInstanceState) {
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stackId = getActivity().getIntent().getLongExtra(Stacks.STACK_ID, -1);
        getLoaderManager().initLoader(StackQuery._TOKEN, null, this);
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
                    Uri uri = Stacks.uriToStack(stackId);
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
                        final Uri data = Stacks.uriToStack(stackId);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ContentValues values = new ContentValues();
                                values.put(Stacks.STACK_DELETED, true);
                                context.getContentResolver().update(data, values, null, null);
                                PreferencesUtils.notifyDeleted(getActivity());
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

    interface StackQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Stacks.STACK_TITLE,
                Stacks.STACK_LANGUAGE,
                Stacks.STACK_COLOR
        };

        int TITLE = 0;
        int LANGUAGE = 1;
        int COLOR = 2;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                Stacks.uriToStack(stackId),
                StackQuery.COLUMNS,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        if (0 == query.getCount()) {
            throw new IllegalArgumentException("Loader was failed. (no such stack)");
        }
        query.moveToFirst();
        title = query.getString(StackQuery.TITLE);
        language = query.getString(StackQuery.LANGUAGE);
        color = query.getInt(StackQuery.COLOR);
        etTitle.setText(title);
        etLanguage.setText(language);
        ibPickColor.setImageDrawable(new ColorDrawable(color));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
