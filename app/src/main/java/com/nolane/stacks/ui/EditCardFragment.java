package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.Card;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CardsDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.schedulers.Schedulers;

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

    private Card card;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        card = (Card) getActivity().getIntent().getSerializableExtra(CardsDatabase.Tables.CARDS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_card, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.edit));

        etFront.setText(card.front);
        etFront.requestFocus();
        etBack.setText(card.back);

//            todo:...
//            InputFilter[] filters = new InputFilter[1];
//            filters[0] = new InputFilter.LengthFilter(Cards.MAX_FRONT_LEN);
//            etFront.setFilters(filters);
//
//            filters = new InputFilter[1];
//            filters[0] = new InputFilter.LengthFilter(Cards.MAX_BACK_LEN);
//            etBack.setFilters(filters);

        return view;
    }

    @OnClick(R.id.btn_done)
    public void finishEditing(View v) {
        final String newFront = etFront.getText().toString();
        final String newBack = etBack.getText().toString();
        if (!newFront.equals(card.front) || !newBack.equals(card.back)) {
            CardsDAO.getInstance().changeCard(card.id, newFront, newBack)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        getActivity().finish();
    }

    @OnEditorAction(R.id.et_back)
    public boolean finishEditing() {
        finishEditing(btnDone);
        return true;
    }
}
