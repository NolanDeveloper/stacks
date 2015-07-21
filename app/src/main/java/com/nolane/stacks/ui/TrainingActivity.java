package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This activity is for training in cards of certain stack. <br>
 * Training is the process when activity is showing the front of card to the user and
 * the user is trying to guess what is on the back of the card.
 */
public class TrainingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (-1 == getIntent().getLongExtra(Stacks.STACK_ID, -1)) {
            throw new IllegalArgumentException("You must pass stack id to start this activity.");
        }
        if (null == savedInstanceState) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new TrainingFragment())
                    .commit();
        }
    }
}