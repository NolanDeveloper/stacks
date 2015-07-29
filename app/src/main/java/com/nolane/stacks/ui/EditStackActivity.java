package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.CardsDatabase.Tables;
import com.nolane.stacks.provider.Stack;

/**
 * This activity allows user to edit stack.
 */
public class EditStackActivity extends AppCompatActivity {
    public static void start(@NonNull Context context, @NonNull Stack stack) {
        Intent intent = new Intent(context, EditStackActivity.class);
        intent.putExtra(Tables.STACKS, stack);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == getIntent().getSerializableExtra(Tables.STACKS)) {
            throw new IllegalArgumentException("You must pass stack to start this activity.");
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_root);
        if (null == fragment) {
            fragment = new EditStackFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, fragment)
                    .commit();
        }
    }
}
