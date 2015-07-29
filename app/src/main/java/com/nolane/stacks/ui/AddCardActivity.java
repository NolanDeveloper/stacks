package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDatabase.StacksColumns;

/**
 * This activity is for adding new cards into existing stack.
 */
public class AddCardActivity extends AppCompatActivity {
    public static void start(@NonNull Context context, long stackId) {
        Intent intent = new Intent(context, AddCardActivity.class);
        intent.putExtra(StacksColumns.STACK_ID, stackId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (-1 == getIntent().getLongExtra(StacksColumns.STACK_ID, -1)) {
            throw new IllegalArgumentException("You must pass stack id to start this activity.");
        }

//        todo: check if up back button works without this code
//        ActionBar actionBar = getActionBar();
//        if (null != actionBar) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_root);
        if (null == fragment) {
            fragment = new AddCardFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, fragment)
                    .commit();
        }
    }
}
