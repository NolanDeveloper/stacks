package com.nolane.stacks.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDatabase.StacksColumns;

/**
 * This activity is for training in cards of certain stack. <br>
 * Training is the process when activity is showing the front of card to the user and
 * the user is trying to guess what is on the back of the card.
 */
public class TrainingActivity extends AppCompatActivity {
    public static void start(@NonNull Context context, long stackId) {
        Intent intent = new Intent(context, TrainingActivity.class);
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
        if (null == getFragmentManager().findFragmentById(R.id.fl_root)) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new TrainingFragment())
                    .commit();
        }
    }
}