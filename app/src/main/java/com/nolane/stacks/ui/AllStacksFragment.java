package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nolane.stacks.R;
import com.nolane.stacks.utils.RecyclerCursorAdapter;
import com.nolane.stacks.utils.UriUtils;

import static com.nolane.stacks.provider.CardsContract.*;

/**
 * This fragment shows list of all stacks. When user clicks at one of them {@link EditStackActivity}
 * is opened to allow editing.
 */
public class AllStacksFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private class StacksAdapter extends RecyclerCursorAdapter<StacksAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View vRoot;
            public TextView tvTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                vRoot = itemView;
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }

        public StacksAdapter(@Nullable Cursor query) {
            super(query);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stack, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            query.moveToPosition(position);
            final long id = query.getLong(StacksQuery.ID);
            final String title = query.getString(StacksQuery.TITLE);
            final Uri thisStack = ContentUris.withAppendedId(Stacks.CONTENT_URI, id);
            holder.tvTitle.setText(title);
            holder.vRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), EditStackActivity.class);
                    Uri data = UriUtils.insertParameter(thisStack, Stacks.STACK_TITLE, title);
                    intent.setData(data);
                    startActivity(intent);
                }
            });
        }
    }

    // UI elements.
    private RecyclerView rvStacks;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_all_stacks, container, false);
        rvStacks = (RecyclerView) view.findViewById(R.id.rv_stacks);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        rvStacks.setLayoutManager(new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
        rvStacks.setAdapter(new StacksAdapter(null));

        fab.setOnClickListener(this);
        getLoaderManager().initLoader(StacksQuery._TOKEN, null, this);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), CreateStackActivity.class);
        startActivity(intent);
    }

    private interface StacksQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Stacks.STACK_ID,
                Stacks.STACK_TITLE
        };

        int ID = 0;
        int TITLE = 1;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Stacks.CONTENT_URI, StacksQuery.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor query) {
        if (null == query) {
            throw new IllegalArgumentException("Loader was failed. (query = null)");
        }
        ((StacksAdapter) rvStacks.getAdapter()).setCursor(query);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
