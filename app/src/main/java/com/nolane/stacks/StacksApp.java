package com.nolane.stacks;

import android.app.Application;

import com.nolane.stacks.provider.CardsDAO;

public class StacksApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CardsDAO.init(this);
    }
}
