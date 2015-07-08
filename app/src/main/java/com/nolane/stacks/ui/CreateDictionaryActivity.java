package com.nolane.stacks.ui;

import android.os.Bundle;

/**
 * This activity is for creating new dictionaries.
 */
public class CreateDictionaryActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainFragment(new CreateDictionaryFragment());
    }
}
