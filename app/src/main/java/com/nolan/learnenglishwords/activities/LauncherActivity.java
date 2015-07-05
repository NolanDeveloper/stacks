package com.nolan.learnenglishwords.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.core.BusinessLogic;
import com.nolan.learnenglishwords.model.Dictionary;

import java.sql.SQLException;

public class LauncherActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if ((null != actionBar) && actionBar.isShowing())
            actionBar.hide();
        setContentView(R.layout.launcher_layout);
        BusinessLogic businessLogic = BusinessLogic.GetInstance(this);
        Cursor dictionaries = null;
        try {
            dictionaries = businessLogic.queryDictionaries();
            if (0 == dictionaries.getCount()) {
                dictionaries.close();
                startActivity(new Intent(getBaseContext(), CreateFirstDictionaryActivity.class));
                return;
            }
            dictionaries.moveToFirst();
            Dictionary first = new Dictionary(dictionaries);
            dictionaries.close();
            Intent intent = new Intent(getBaseContext(), TrainingActivity.class);
            intent.putExtra(TrainingActivity.ARG_DICTIONARY, first);
            startActivity(intent);
        } catch (SQLException e) {
            e.printStackTrace();
            finish();
        } finally {
            if ((null != dictionaries) && (!dictionaries.isClosed()))
                dictionaries.close();
        }
    }
}
