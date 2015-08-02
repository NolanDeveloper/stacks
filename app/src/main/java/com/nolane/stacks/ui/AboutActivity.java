package com.nolane.stacks.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        Fragment f = getFragmentManager().findFragmentById(R.id.fl_root);
        if (null == f) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new AboutFragment())
                    .commit();
        }
    }
}
