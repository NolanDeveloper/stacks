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
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;

/**
 * This fragment allows user to edit cards(eg to change front, to change back).
 * The activity that uses this fragment must specify data with the type
 * {@link CardsContract.Card#CONTENT_ITEM_TYPE} and contain parameters:
 * {@link CardsContract.Card#CARD_FRONT}, {@link CardsContract.Card#CARD_BACK},
 * {@link CardsContract.Card#CARD_SCRUTINY}. This fragment is used in
 * conjunction with {@link EditCardActivity}.
 */
public class EditCardFragment extends Fragment implements View.OnClickListener {
    // UI elements.
    private TextView tvProgress;
    private EditText etFront;
    private EditText etBack;
    private Button btnDone;

    // Actual values of card that are in database.
    private long id;
    private String front;
    private String back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_card, container, false);
        tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        Uri data = getActivity().getIntent().getData();
        id = Long.parseLong(data.getLastPathSegment());
        String scrutiny = data.getQueryParameter(CardsContract.Card.CARD_SCRUTINY);
        tvProgress.setText(scrutiny);
        front = data.getQueryParameter(CardsContract.Card.CARD_FRONT);
        etFront.setText(front);
        back = data.getQueryParameter(CardsContract.Card.CARD_BACK);
        etBack.setText(back);

        btnDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        final String newFront = etFront.getText().toString();
        final String newBack = etBack.getText().toString();
        if (!newFront.equals(front) || !newBack.equals(back)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri uri = getActivity().getIntent().getData();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CardsContract.Card.CARD_FRONT, newFront);
                    contentValues.put(CardsContract.Card.CARD_BACK, newBack);
                    EditCardFragment.this.getActivity().getContentResolver()
                            .update(uri, contentValues, null, null);
                }
            }).run();
        }
        getActivity().onBackPressed();
    }
}
