package com.nolan.learnenglishwords.activities;

import android.os.Bundle;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.fragments.TrainingCardFragment;
import com.nolan.learnenglishwords.fragments.TrainingIntroFragment;
import com.nolan.learnenglishwords.model.Dictionary;

public class TrainingActivity extends BaseNavigationDrawerActivity {
    public static final String ARG_DICTIONARY = "dictionary";

    private Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionary = (Dictionary) getIntent().getSerializableExtra(ARG_DICTIONARY);
        setMainFragment(TrainingIntroFragment.GetInstance(dictionary));
    }

    public void startTraining() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fl_main_content, TrainingCardFragment.GetInstance(dictionary))
                .addToBackStack(null)
                .commit();
    }
}
