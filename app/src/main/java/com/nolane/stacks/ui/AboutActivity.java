package com.nolane.stacks.ui;

import android.os.Bundle;

public class AboutActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == getMainFragment()) {
            setMainFragment(new AboutFragment());
        }
    }
}
