package com.nolan.learnenglishwords.controller.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.controller.fragments.TrainingCardFragment;
import com.nolan.learnenglishwords.controller.fragments.TrainingIntroFragment;

public class TrainingActivity extends BaseNavigationDrawerActivity implements TrainingIntroFragment.TrainingStarter {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMainFragment(new TrainingIntroFragment());
    }

    @Override
    public void startTraining(@NonNull String dictionaryTitle) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_content, TrainingCardFragment.GetInstance(dictionaryTitle))
                .addToBackStack(null)
                .commit();
    }
}
