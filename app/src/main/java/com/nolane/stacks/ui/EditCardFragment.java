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

import static com.nolane.stacks.provider.CardsContract.Cards;

/**
 * This fragment allows user to edit cards.
 * Requires: <br>
 * data type: {@link Cards#CONTENT_ITEM_TYPE} <br>
 * data parameter: {@link Cards#CARD_FRONT} <br>
 * data parameter: {@link Cards#CARD_BACK}
 */
public class EditCardFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    // UI elements.
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
        etFront = (EditText) view.findViewById(R.id.et_front);
        etBack = (EditText) view.findViewById(R.id.et_back);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        Uri data = getActivity().getIntent().getData();
        id = Long.parseLong(data.getLastPathSegment());
        front = data.getQueryParameter(Cards.CARD_FRONT);
        back = data.getQueryParameter(Cards.CARD_BACK);

        etBack.setOnEditorActionListener(this);

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

        btnDone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        UriUtils.checkDataTypeOrThrow(getActivity(), Cards.CONTENT_ITEM_TYPE);
        UriUtils.checkSpecifiesParameterOrThrow(getActivity(), Cards.CARD_FRONT);
        UriUtils.checkSpecifiesParameterOrThrow(getActivity(), Cards.CARD_BACK);
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
                    contentValues.put(Cards.CARD_FRONT, newFront);
                    contentValues.put(Cards.CARD_BACK, newBack);
                    EditCardFragment.this.getActivity().getContentResolver()
                            .update(uri, contentValues, null, null);
                }
            }).run();
        }
        getActivity().finish();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        onClick(btnDone);
        return true;
    }
}
