package com.nolane.stacks;

import android.app.Application;

import com.nolane.stacks.provider.CardsDAO;
import com.nolane.stacks.utils.PrefUtils;

public class StacksApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CardsDAO.init(this);
        PrefUtils.init(this);
    }
}
