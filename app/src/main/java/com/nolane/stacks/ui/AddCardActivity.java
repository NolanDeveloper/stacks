package com.nolane.stacks.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This activity is for adding new cards into existing stack.
 */
public class AddCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (-1 == getIntent().getLongExtra(Stacks.STACK_ID, -1)) {
            throw new IllegalArgumentException("You must pass stack id to start this activity.");
        }

        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (null == savedInstanceState) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new AddCardFragment())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
