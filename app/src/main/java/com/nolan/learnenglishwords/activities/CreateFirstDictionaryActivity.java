package com.nolan.learnenglishwords.activities;

import android.app.Activity;
import android.os.Bundle;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.fragments.CreateDictionaryFragment;

public class CreateFirstDictionaryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_first_dictionary_activity);
        getFragmentManager().findFragmentById(R.id.fr_root);
    }
}
