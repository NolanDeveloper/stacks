package com.nolan.learnenglishwords.controller.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;

import com.nolan.learnenglishwords.R;
import com.nolan.learnenglishwords.controller.fragments.NavigationFragment;

/**
 * This activity is base class for activities with navigation drawer. It assumes all
 * responsibilities for the processing of the navigation drawer including action
 * bar toggle.
 * <p>
 * The ui of it consists of two fragments: navigation fragment and main content fragment.
 * <p>
 * Navigation fragment is {@link NavigationFragment} class. So if you want to change
 * navigation drawer look there.
 * <p>
 * Main content fragment places on the whole screen. Derivative classes can specify it using
 * {@link #setMainFragment(Fragment)}.
 */
public abstract class BaseNavigationDrawerActivity extends Activity {
    // Fragment that places in the navigation area. It shows up
    // when user swipes from the left side of the screen.
    private NavigationFragment navigationFragment;

    private DrawerLayout dlRoot;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    protected BaseNavigationDrawerActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_navigation_drawer);

        ActionBar actionBar = getActionBar();
        if (null != actionBar)
            actionBar.setTitle(getString(R.string.title_new_dictionary));

        navigationFragment = new NavigationFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_navigation, navigationFragment)
                .commit();

        dlRoot = (DrawerLayout) findViewById(R.id.dl_root);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                dlRoot,
                null,
                0,
                0) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                ActionBar actionBar = getActionBar();
                if (null != actionBar)
                    actionBar.setTitle(getString(R.string.title_new_dictionary));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActionBar actionBar = getActionBar();
                if (null != actionBar)
                    actionBar.setTitle(getString(R.string.title_dictionaries));
            }
        };
        dlRoot.setDrawerListener(actionBarDrawerToggle);
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the main fragment of the activity. This function should be used by derivative
     * classes to specify main fragment.
     * @param fragment Fragment to placed as main.
     */
    public void setMainFragment(@NonNull Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_content, fragment)
                .commit();
    }
}
