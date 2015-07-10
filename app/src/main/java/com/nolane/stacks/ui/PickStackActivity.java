package com.nolane.stacks.ui;

import android.os.Bundle;

/**
 * To start training we need to know which stack user wants to train. So
 * we have this activity that figures this out.
 */
public class PickStackActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            setMainFragment(new PickStackFragment());
        }
    }
}
