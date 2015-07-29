package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.nolane.stacks.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * This activity is base class for activities with navigation drawer. It assumes all
 * responsibilities for the processing of the navigation drawer including action
 * bar toggle.
 * <p/>
 * The ui of it consists of two fragments: navigation fragment and main content fragment.
 * <p/>
 * Navigation fragment is {@link NavigationFragment} class. So if you want to change
 * navigation drawer look there.
 * <p/>
 * Main content fragment places on the whole screen. Derivative classes can specify it using
 * {@link #setMainFragment(Fragment)}.
 */
public abstract class BaseNavigationDrawerActivity extends AppCompatActivity {
    private static final String EXTRA_IS_DRAWER_OPENED = "is.drawer.opened";

    // Fragment that places in the navigation area. It shows up
    // when user swipes from the left side of the screen.
    private NavigationFragment navigationFragment;

    @Bind(R.id.dl_root)
    DrawerLayout dlRoot;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private boolean isDrawerOpened;

    protected BaseNavigationDrawerActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_base_navigation_drawer);
        ButterKnife.bind(this);

        if (null != savedInstanceState) {
            isDrawerOpened = savedInstanceState.getBoolean(EXTRA_IS_DRAWER_OPENED);
        }

        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.fl_navigation);
        if (null == navigationFragment) {
            navigationFragment = new NavigationFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_navigation, navigationFragment)
                    .commit();
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, dlRoot, null, 0, 0) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                ActionBar actionBar = getSupportActionBar();
                if (null != actionBar) {
                    actionBar.setTitle(getTitle());
                }
                isDrawerOpened = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActionBar actionBar = getSupportActionBar();
                if (null != actionBar) {
                    actionBar.setTitle(getString(R.string.title_stacks));
                }
                isDrawerOpened = true;
            }
        };
        dlRoot.setDrawerListener(actionBarDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            if (isDrawerOpened) {
                actionBar.setTitle(getString(R.string.title_stacks));
            } else {
                actionBar.setTitle(getTitle());
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (!isDrawerOpened) {
            ActionBar actionBar = getSupportActionBar();
            if (null != actionBar)
                actionBar.setTitle(title);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_IS_DRAWER_OPENED, isDrawerOpened);
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
     *
     * @param fragment Fragment to placed as main.
     */
    protected void setMainFragment(@NonNull Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_main_content, fragment)
                .commit();
    }

    /**
     * Returns the fragment which is on the place of main view.
     * @return the fragment which is on the place of main view.
     */
    protected Fragment getMainFragment() {
        return getFragmentManager().findFragmentById(R.id.fl_main_content);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpened) {
            hideNavigationDrawer();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Hides navigation drawer.
     */
    public void hideNavigationDrawer() {
        dlRoot.closeDrawers();
    }
}
