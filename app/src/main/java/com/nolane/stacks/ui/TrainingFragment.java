package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.Card;
import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.provider.CardsDatabase.StacksColumns;
import com.nolane.stacks.provider.CursorWrapper;
import com.nolane.stacks.utils.GeneralUtils;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * This fragment is used for training process. It is used in conjunction with
 * {@link TrainingActivity}.
 */
public class TrainingFragment extends Fragment {
    private long stackId;

    // UI elements.
    @Bind(R.id.v_progress_indicator)
    View vProgressIndicator;
    @Bind(R.id.tv_front)
    TextView tvFront;
    @Bind(R.id.et_back)
    EditText etBack;
    @Bind(R.id.btn_done)
    Button btnDone;

    private CursorWrapper<Card> studyCards;

    private Random random = new Random();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        stackId = getActivity().getIntent().getLongExtra(StacksColumns.STACK_ID, -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.frag_training, container, false);
        ButterKnife.bind(this, view);
        etBack.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                checkUserVariant();
                return true;
            }
        });
        if (null == studyCards) {
            CardsDAO.getInstance()
                    .getCardsToLearn(stackId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<CursorWrapper<Card>>() {
                        @Override
                        public void call(CursorWrapper<Card> cardCursorWrapper) {
                            studyCards = cardCursorWrapper;
                            if (nextCard()) {
                                showCard();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throw new Error(throwable);
                        }
                    });
        } else {
            showCard();
        }
//            todo:
//            InputFilter[] filter = new InputFilter[1];
//            filter[0] = new InputFilter.LengthFilter(Cards.MAX_BACK_LEN);
//            etBack.setFilters(filter);
        return view;
    }

    private boolean nextCard() {
        if (!studyCards.moveToNext()) {
            // todo: show snackbar "well done!"
            getActivity().finish();
            return false;
        }
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    private void showCard() {
        Card card = studyCards.get();
        tvFront.setText(card.front);
        vProgressIndicator.setBackgroundColor(
                GeneralUtils.getColorForProgress(getActivity(), card.progress));
    }

    @OnClick(R.id.btn_done)
    public void checkUserVariant() {
        String userVariant = etBack.getText().toString();
        Card card = studyCards.get();
        if (userVariant.equalsIgnoreCase(card.back)) {
            CardsDAO.getInstance()
                    .promoteCard(card.id)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        } else {
            CardsDAO.getInstance()
                    .returnCard(card.id)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        nextCard();
        showCard();
    }
}
