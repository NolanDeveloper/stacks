package com.nolane.stacks.ui;

import android.net.Uri;
import android.os.Bundle;

import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity is for adding new cards into existing stack. You must specify uri for
 * the stack using {@link android.content.Intent#setData(Uri)}.
 */
public class AddCardActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, CardsContract.Stacks.CONTENT_ITEM_TYPE);
            setMainFragment(new AddCardFragment());
        }
    }
}
