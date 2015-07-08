package com.nolane.stacks.ui;

import android.app.Activity;
import android.os.Bundle;

import com.nolane.stacks.R;

/**
 * This activity is for creating first stack. We decided to make it
 * distinct of {@link CreateStackActivity} because when the user creates
 * the first stack there is no place where he can navigate to. Hence
 * the user does not need to see navigation bar. Hence we must have another
 * activity which is not derivative of {@link BaseNavigationDrawerActivity}.
 */
public class CreateFirstStackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_first_stack);
    }
}
