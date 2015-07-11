package com.nolane.stacks.ui;

import android.os.Bundle;

/**
 * This activity shows to user all cards. The user can remove card or edit it.
 */
public class AllCardsActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            setMainFragment(new AllCardsFragment());
        }
    }
}
