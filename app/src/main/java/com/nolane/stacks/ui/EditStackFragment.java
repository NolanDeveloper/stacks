package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nolane.stacks.R;

import static com.nolane.stacks.provider.CardsContract.*;

/**
 * todo
 */
public class EditStackFragment extends Fragment implements View.OnClickListener {
    // Actual values of stack.
    private String title;

    // UI elements.
    private EditText etTitle;
    private Button btnDone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_stack, container, false);
        etTitle = (EditText) view.findViewById(R.id.tv_title);
        btnDone = (Button) view.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);
        title = getActivity().getIntent().getData().getQueryParameter(Stacks.STACK_TITLE);
        if (null == savedInstanceState) {
            etTitle.setText(title);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        final String newTitle = etTitle.getText().toString();
        if (!newTitle.equals(title)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = getActivity().getIntent().getData();
                    ContentValues values = new ContentValues();
                    values.put(Stacks.STACK_TITLE, newTitle);
                    getActivity().getContentResolver().update(uri, values, null, null);
                }
            }).run();
        }
        getActivity().finish();
    }
}
