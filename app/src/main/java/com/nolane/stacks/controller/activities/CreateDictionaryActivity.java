package com.nolane.stacks.controller.activities;

import android.os.Bundle;

import com.nolane.stacks.controller.fragments.CreateDictionaryFragment;

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
