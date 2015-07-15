package com.nolane.stacks.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.*;

/**
 * todo
 */
public class EditStackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        if (null == savedInstanceState) {
            UriUtils.checkDataTypeOrThrow(this, Stacks.CONTENT_ITEM_TYPE);
            UriUtils.checkSpecifiesParameterOrThrow(this, Stacks.STACK_TITLE);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_root, new EditStackFragment())
                    .commit();
        }
    }
}