package com.nolan.learnenglishwords.controller.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.controller.fragments.TrainingCardFragment;
import com.nolan.learnenglishwords.controller.fragments.TrainingIntroFragment;
import com.nolan.learnenglishwords.model.CardsContract;

/**
 * This activity is for training in cards of certain dictionary. You must specify uri for
 * the dictionary using {@link android.content.Intent#setData(Uri)}.
 * <p>
 * Training is the process when activity is showing the front of card to the user and
 * the user is trying to guess what is on the back of the card.
 */
public class TrainingActivity extends BaseNavigationDrawerActivity implements TrainingIntroFragment.TrainingStarter {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            // We should check that passed data is correct.
            // Firstly it should exist.
            final Uri data = getIntent().getData();
            if (null == data)
                throw new IllegalArgumentException("You must specify data(uri to dictionary) to start this activity.");
            // Secondly it should have proper type.
            final String dataType = getContentResolver().getType(data);
            final String requiredType = CardsContract.Dictionary.CONTENT_ITEM_TYPE;
            if (!requiredType.equals(dataType))
                throw new IllegalArgumentException("Specified data has unknown type. Must be \"" + requiredType + "\".");
            setMainFragment(new TrainingIntroFragment());
        }
    }

    @Override
    public void startTraining(@NonNull String dictionaryTitle) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_content, TrainingCardFragment.getInstance(dictionaryTitle))
                .addToBackStack(null)
                .commit();
    }
}
