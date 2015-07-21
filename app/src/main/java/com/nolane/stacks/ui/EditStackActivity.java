package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This activity allows user to edit stack.
 */
public class EditStackActivity extends AppCompatActivity {
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
                    .replace(R.id.fl_root, new EditStackFragment())
                    .commit();
        }
    }
}
