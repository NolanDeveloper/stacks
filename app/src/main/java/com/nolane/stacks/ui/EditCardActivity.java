package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity allows user to edit cards(eg to change front, to change back).
 * The intent that will call this activity must specify the data. The data must
 * have type {@link CardsContract.Cards#CONTENT_ITEM_TYPE} and contain parameters:
 * {@link CardsContract.Cards#CARD_FRONT}, {@link CardsContract.Cards#CARD_BACK},
 * {@link CardsContract.Cards#CARD_PROGRESS}.
 */
public class EditCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, CardsContract.Cards.CONTENT_ITEM_TYPE);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_FRONT);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_BACK);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_PROGRESS);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new EditCardFragment())
                    .commit();
        }
    }
}
