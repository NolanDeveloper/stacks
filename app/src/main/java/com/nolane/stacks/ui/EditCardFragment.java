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
import com.nolane.stacks.provider.CardsContract;

/**
 * This fragment allows user to edit cards(eg to change front, to change back).
 * The activity that uses this fragment must specify data with the type
 * {@link CardsContract.Cards#CONTENT_ITEM_TYPE} and contain parameters:
 * {@link CardsContract.Cards#CARD_FRONT}, {@link CardsContract.Cards#CARD_BACK},
 * {@link CardsContract.Cards#CARD_PROGRESS}. This fragment is used in
 * conjunction with {@link EditCardActivity}.
 */
public class EditCardFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
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
        String progress = data.getQueryParameter(CardsContract.Cards.CARD_PROGRESS);
        front = data.getQueryParameter(CardsContract.Cards.CARD_FRONT);
        back = data.getQueryParameter(CardsContract.Cards.CARD_BACK);

        etBack.setOnEditorActionListener(this);

        if (null == savedInstanceState) {
            tvProgress.setText(progress);
            etFront.setText(front);
            etBack.setText(back);

            InputFilter[] filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(CardsContract.Cards.MAX_FRONT_LEN);
            etFront.setFilters(filters);

            filters = new InputFilter[1];
            filters[0] = new InputFilter.LengthFilter(CardsContract.Cards.MAX_BACK_LEN);
            etBack.setFilters(filters);
        }

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
                    contentValues.put(CardsContract.Cards.CARD_FRONT, newFront);
                    contentValues.put(CardsContract.Cards.CARD_BACK, newBack);
                    EditCardFragment.this.getActivity().getContentResolver()
                            .update(uri, contentValues, null, null);
                }
            }).run();
        }
        getActivity().onBackPressed();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(null);
        return true;
    }
}
