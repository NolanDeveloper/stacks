package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.provider.Card;
import com.nolane.stacks.provider.CardsDatabase.Tables;

/**
 * This activity allows user to edit cards.
 */
public class EditCardActivity extends AppCompatActivity {
    public static void start(@NonNull Context context, @NonNull Card card) {
        Intent intent = new Intent(context, EditCardActivity.class);
        intent.putExtra(Tables.CARDS, card);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == getIntent().getSerializableExtra(Tables.CARDS)) {
            throw new IllegalArgumentException("You must pass card to start this activity.");
        }

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fl_root);
        if (null == fragment) {
            fragment = new EditCardFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, fragment)
                    .commit();
        }
    }
}
