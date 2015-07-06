package com.nolan.learnenglishwords.controller.activities;

import android.net.Uri;
import android.os.Bundle;

import com.nolan.learnenglishwords.controller.fragments.AddCardFragment;
import com.nolan.learnenglishwords.model.CardsContract;

public class AddCardActivity extends BaseNavigationDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Uri data = getIntent().getData();
        if (null == data)
            throw new IllegalArgumentException("You must specify data(uri to dictionary) to start this activity.");
        final String dataType = getContentResolver().getType(data);
        final String requiredType = CardsContract.Dictionary.CONTENT_ITEM_TYPE;
        if (!requiredType.equals(dataType))
            throw new IllegalArgumentException("Specified data has unknown type. Must be \"" + requiredType + "\".");
        setMainFragment(new AddCardFragment());
    }
}
