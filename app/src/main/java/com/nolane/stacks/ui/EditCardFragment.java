package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nolane.stacks.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

import static com.nolane.stacks.provider.CardsContract.Cards;

/**
 * This fragment allows user to edit cards.
 */
public class EditCardFragment extends Fragment {
    // UI elements.
    @Bind(R.id.et_front)
    EditText etFront;
    @Bind(R.id.et_back)
    EditText etBack;
    @Bind(R.id.btn_done)
    Button btnDone;

    // Actual values of card that are in database.
    private long id;
    private String front;
    private String back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_card, container, false);
        ButterKnife.bind(this, view);

        Uri data = getActivity().getIntent().getData();
        id = Long.parseLong(data.getLastPathSegment());
        front = data.getQueryParameter(Cards.CARD_FRONT);
        back = data.getQueryParameter(Cards.CARD_BACK);

        if (null == savedInstanceState) {
            etFront.setText(front);
            etBack.setText(back);

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(Cards.MAX_FRONT_LEN);
            etFront.setFilters(filters);

            filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(Cards.MAX_BACK_LEN);
            etBack.setFilters(filters);
        }

        return view;
    }

    @OnClick(R.id.btn_done)
    public void finishEditing(View v) {
        final String newFront = etFront.getText().toString();
        final String newBack = etBack.getText().toString();
        if (!newFront.equals(front) || !newBack.equals(back)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = getActivity().getIntent().getData();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(Cards.CARD_FRONT, newFront);
                    contentValues.put(Cards.CARD_BACK, newBack);
                    EditCardFragment.this.getActivity().getContentResolver()
                            .update(uri, contentValues, null, null);
                }
            }).run();
        }
        getActivity().finish();
    }

    @OnEditorAction(R.id.et_back)
    public boolean finishEditing() {
        finishEditing(btnDone);
        return true;
    }
}
