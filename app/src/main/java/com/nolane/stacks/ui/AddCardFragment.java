package com.nolane.stacks.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.Card;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CardsDatabase;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment is for adding new cards to existing stack. It is used in
 * conjunction with {@link AddCardActivity}.
 */
public class AddCardFragment extends Fragment {
    // Id of the specified stack.
    private long stackId;

    // UI elements.
    @Bind(R.id.et_front)
    EditText etFront;
    @Bind(R.id.et_back)
    EditText etBack;
    @Bind(R.id.ib_bidirectional_help)
    ImageButton ibBidirectionalHelp;
    @Bind(R.id.cb_bidirectional)
    CheckBox cbBidirectional;
    @Bind(R.id.btn_done)
    Button btnDone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        stackId = getActivity().getIntent().getLongExtra(CardsDatabase.StacksColumns.STACK_ID, -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_add_card, container, false);
        ButterKnife.bind(this, view);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCard();
            }
        });
        etBack.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                addCard();
                return true;
            }
        });

        getActivity().setTitle(getString(R.string.add_card));

        if (null == savedInstanceState) {
            etFront.setText("");

//          todo: add filter everywhere... or notify user with .setError() on TextInputLayout
//            InputFilter[] filters = new InputFilter[1];
//            filters[0] = new InputFilter.LengthFilter(Cards.MAX_FRONT_LEN);
//            etFront.setFilters(filters);
//
//            filters = new InputFilter[1];
//            filters[0] = new InputFilter.LengthFilter(Cards.MAX_BACK_LEN);
//            etBack.setFilters(filters);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.ib_bidirectional_help)
    public void showBidirectionalHelpDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.bidirectional)
                .setMessage(getString(R.string.bidirectional_help))
                .setNegativeButton(R.string.ok, null)
                .show();
    }

    /**
     * Adds card into database according to the state of the views.
     */
    public void addCard() {
        final String front = etFront.getText().toString();
        final String back = etBack.getText().toString();
        final ContentResolver resolver = getActivity().getContentResolver();
        btnDone.setActivated(false);
        etFront.setActivated(false);
        etBack.setActivated(false);
        CardsDAO.getInstance()
                .addCard(stackId, cbBidirectional.isChecked(), front, back)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Card[]>() {
                    @Override
                    public void call(Card[] cards) {
                        btnDone.setActivated(true);
                        etFront.setActivated(true);
                        etBack.setActivated(true);
                        etFront.getText().clear();
                        etBack.getText().clear();
                        etFront.requestFocus();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // something went wrong...
                        // todo: think about it
                    }
                });
    }
}
