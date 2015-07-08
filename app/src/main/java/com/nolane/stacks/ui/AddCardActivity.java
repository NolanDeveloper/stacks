package com.nolane.stacks.ui;

import android.net.Uri;
import android.os.Bundle;

import com.nolane.stacks.provider.CardsContract;

/**
 * This activity is for adding new cards into existing dictionary. You must specify uri for
 * the dictionary using {@link android.content.Intent#setData(Uri)}.
 */
public class AddCardActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We should check that passed data is correct.
        // Firstly it should exist.
        final Uri data = getIntent().getData();
        if (null == data)
            throw new IllegalArgumentException("You must specify data(uri to dictionary) to start this activity.");
        // Secondly it should have proper type.
        final String dataType = getContentResolver().getType(data);
        final String requiredType = CardsContract.Dictionary.CONTENT_ITEM_TYPE;
        if (!requiredType.equals(dataType))
            throw new IllegalArgumentException("Specified data has unknown type. Must be \"" + requiredType + "\".");
        setMainFragment(new AddCardFragment());
    }
}
