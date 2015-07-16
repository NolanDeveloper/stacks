package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;

/**
 * This activity shows to user all cards. The user can remove card or edit it.
 */
public class AllCardsActivity extends BaseNavigationDrawerActivity {
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            fragment = new AllCardsFragment();
            setMainFragment(fragment);
        } else {
            fragment = getMainFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        fragment.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }
}
