package com.nolan.learnenglishwords.activities;

import android.os.Bundle;

import com.nolan.learnenglishwords.fragments.CreateDictionaryFragment;

public class CreateDictionaryActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainFragment(new CreateDictionaryFragment());
    }
}
