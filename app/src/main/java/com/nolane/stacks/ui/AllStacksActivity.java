package com.nolane.stacks.ui;

import android.os.Bundle;

public class AllStacksActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            setMainFragment(new AllStacksFragment());
        }
    }
}
