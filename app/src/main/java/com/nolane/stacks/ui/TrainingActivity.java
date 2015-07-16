package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.Stacks;

/**
 * This activity is for training in cards of certain stack. <br>
 * Training is the process when activity is showing the front of card to the user and
 * the user is trying to guess what is on the back of the card. <br>
 * Required: <br>
 * data type: {@link Stacks#CONTENT_ITEM_TYPE}
 */
public class TrainingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, Stacks.CONTENT_ITEM_TYPE);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new TrainingFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.mi_help == item.getItemId()) {

        }
        return false;
    }
}