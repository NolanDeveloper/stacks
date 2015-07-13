package com.nolane.stacks.ui;

import android.os.Bundle;

import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity allows user to edit cards(eg to change front, to change back).
 * The intent that will call this activity must specify the data. The data must
 * have type {@link CardsContract.Cards#CONTENT_ITEM_TYPE} and contain parameters:
 * {@link CardsContract.Cards#CARD_FRONT}, {@link CardsContract.Cards#CARD_BACK},
 * {@link CardsContract.Cards#CARD_PROGRESS}.
 */
public class EditCardActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, CardsContract.Cards.CONTENT_ITEM_TYPE);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_FRONT);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_BACK);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Cards.CARD_PROGRESS);
            setMainFragment(new EditCardFragment());
        }
    }
}
