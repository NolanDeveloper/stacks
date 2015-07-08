package com.nolane.stacks.ui;

import android.os.Bundle;

/**
 * This activity is for creating new stacks.
 */
public class CreateStackActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            setMainFragment(new CreateStackFragment());
        }
    }
}
