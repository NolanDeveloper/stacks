package com.nolan.learnenglishwords.controller.activities;

import android.os.Bundle;

import com.nolan.learnenglishwords.controller.fragments.CreateDictionaryFragment;

public class CreateDictionaryActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainFragment(new CreateDictionaryFragment());
    }
}
