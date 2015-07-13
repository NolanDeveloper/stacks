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
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.mi_training:
                if (getActivity() instanceof PickStackActivity) {
                    ((PickStackActivity) getActivity()).hideNavigationDrawer();
                    return true;
                }
                intent = new Intent(getActivity().getBaseContext(), PickStackActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.mi_cards:
                if (getActivity() instanceof AllCardsActivity) {
                    ((AllCardsActivity) getActivity()).hideNavigationDrawer();
                    return true;
                }
                intent = new Intent(getActivity().getBaseContext(), AllCardsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.mi_stacks:
                if (getActivity() instanceof AllStacksActivity) {
                    ((AllStacksActivity) getActivity()).hideNavigationDrawer();
                    return true;
                }
                intent = new Intent(getActivity().getBaseContext(), AllStacksActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
        return true;
    }
}
