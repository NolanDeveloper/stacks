package com.nolane.stacks.ui;

import android.os.Bundle;

import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity allows user to edit cards(eg to change front, to change back).
 * The intent that will call this activity must specify the data. The data must
 * have type {@link CardsContract.Card#CONTENT_ITEM_TYPE} and contain parameters:
 * {@link CardsContract.Card#CARD_FRONT}, {@link CardsContract.Card#CARD_BACK},
 * {@link CardsContract.Card#CARD_SCRUTINY}.
 */
public class EditCardActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, CardsContract.Card.CONTENT_ITEM_TYPE);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Card.CARD_FRONT);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Card.CARD_BACK);
            UriUtils.checkSpecifiesParameterOrThrow(this, CardsContract.Card.CARD_SCRUTINY);
            setMainFragment(new EditCardFragment());
        }
    }
}
