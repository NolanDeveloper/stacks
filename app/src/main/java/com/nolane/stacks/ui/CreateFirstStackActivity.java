package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;

/**
 * This activity is for creating first stack. We decided to make it
 * distinct of {@link CreateStackActivity} because when the user creates
 * the first stack there is no place where he can navigate to. Hence
 * the user does not need to see navigation sidebar. Hence we must have another
 * activity which is not derivative of {@link BaseNavigationDrawerActivity}.
 */
public class CreateFirstStackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_create_first_stack);
    }
}
