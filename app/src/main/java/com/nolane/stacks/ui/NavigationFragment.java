package com.nolane.stacks.ui;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nolane.stacks.R;

/**
 * This fragment is used for navigation drawer.
 */
public class NavigationFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {
    // UI elements.
    private NavigationView nv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.frag_navigation, container, false);
        nv = (NavigationView) view.findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(this);
        return view;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Class itemActivityClass;
        switch (menuItem.getItemId()) {
            case R.id.mi_training:
                itemActivityClass = PickStackActivity.class;
                break;
            case R.id.mi_cards:
                itemActivityClass = AllCardsActivity.class;
                break;
            case R.id.mi_stacks:
                itemActivityClass = AllStacksActivity.class;
                break;
            default:
                throw new IllegalArgumentException("Unknown menu item id.");
        }
        if (itemActivityClass.isInstance(getActivity())) {
            ((BaseNavigationDrawerActivity) getActivity()).hideNavigationDrawer();
        } else {
            Intent intent = new Intent(getActivity().getBaseContext(), itemActivityClass);
            startActivity(intent);
            getActivity().finish();
        }
        return true;
    }
}
