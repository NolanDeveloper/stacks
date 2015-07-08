package com.nolane.stacks.ui;

import android.net.Uri;
import android.os.Bundle;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsContract;
import com.nolane.stacks.utils.UriUtils;

/**
 * This activity is for training in cards of certain stack. You must specify uri for
 * the stack using {@link android.content.Intent#setData(Uri)}.
 * <p>
 * Training is the process when activity is showing the front of card to the user and
 * the user is trying to guess what is on the back of the card.
 */
public class TrainingActivity extends BaseNavigationDrawerActivity implements TrainingIntroFragment.TrainingStarter {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, CardsContract.Stacks.CONTENT_ITEM_TYPE);
            setMainFragment(new TrainingIntroFragment());
        }
    }

    @Override
    public void startTraining() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_content, new TrainingCardFragment())
                .addToBackStack(null)
                .commit();
    }
}