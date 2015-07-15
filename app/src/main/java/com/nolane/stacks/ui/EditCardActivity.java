package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.Cards;

/**
 * This activity allows user to edit cards. <br>
 * Requires: <br>
 * data type: {@link Cards#CONTENT_ITEM_TYPE} <br>
 * data parameter: {@link Cards#CARD_FRONT} <br>
 * data parameter: {@link Cards#CARD_BACK}
 */
public class EditCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, Cards.CONTENT_ITEM_TYPE);
            UriUtils.checkSpecifiesParameterOrThrow(this, Cards.CARD_FRONT);
            UriUtils.checkSpecifiesParameterOrThrow(this, Cards.CARD_BACK);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new EditCardFragment())
                    .commit();
        }
    }
}
