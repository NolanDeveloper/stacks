package com.nolane.stacks.ui;

import android.os.Bundle;

/**
 * This activity shows list of all stacks. When user click at one of them {@link EditStackActivity}
 * is opened to allow editing.
 */
public class AllStacksActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == getMainFragment()) {
            setMainFragment(new AllStacksFragment());
        }
    }
}
