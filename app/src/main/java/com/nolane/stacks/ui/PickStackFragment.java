package com.nolane.stacks.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nolane.stacks.R;
import static com.nolane.stacks.provider.CardsContract.*;
import com.nolane.stacks.utils.MetricsUtils;
import com.nolane.stacks.utils.UriUtils;

/**
 * This fragment must find out which stack user wants to train and then
 * start TrainingActivity with this stack as data.
 */
public class PickStackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Adapter for the RecyclerView.
     */
    public class StacksAdapter extends RecyclerView.Adapter<StacksAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public View root;
            public TextView tvTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                root = itemView;
                tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            }
        }

        private Cursor query;

        public StacksAdapter(@Nullable Cursor query) {
            super();
            this.query = query;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stack, parent, false);
            return new ViewHolder(item);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            query.moveToPosition(position);
            final long id = query.getLong(StacksQuery.ID);
            final String title = query.getString(StacksQuery.TITLE);
            final int count = query.getInt(StacksQuery.COUNT_CARDS);
            holder.tvTitle.setText(title);
            if (0 == count) {
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(getView(), getString(R.string.no_cards), Snackbar.LENGTH_SHORT)
                                .setAction(R.string.add_card, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), AddCardActivity.class);
                                        Uri data = ContentUris.withAppendedId(Stacks.CONTENT_URI, id);
                                        intent.setData(data);
                                        intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        startActivity(intent);
                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.snack_bar_positive))
                                .show();
                    }
                });
            } else {
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TrainingActivity.class);
                        Uri data = ContentUris.withAppendedId(Stacks.CONTENT_URI, id);
                        data = UriUtils.insertParameter(data, Stacks.STACK_TITLE, title);
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (null == query) {
                return 0;
            } else {
                return query.getCount();
            }
        }

        public void setCursor(@Nullable Cursor query) {
            if (this.query == query)
                return;
            if (null != this.query) {
                this.query.close();
            }
            this.query = query;
            notifyDataSetChanged();
        }
    }

    // UI elements.
    private RecyclerView rvStacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_pick_stack, container, false);
        rvStacks = (RecyclerView) view.findViewById(R.id.rv_stacks);
        rvStacks.setLayoutManager(new GridLayoutManager(
                getActivity(), 2, GridLayoutManager.VERTICAL, false));
        rvStacks.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        super.getItemOffsets(outRect, view, parent, state);
                        int margin = MetricsUtils.convertDpToPx(4);
                        outRect.top = margin;
                        outRect.right = margin;
                        outRect.bottom = margin;
                        outRect.left = margin;
                    }
                });
                getActivity().setTitle(getString(R.string.choose_stack));
        rvStacks.setAdapter(new StacksAdapter(null));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(StacksQuery._TOKEN, null, this);
    }

    private interface StacksQuery {
        int _TOKEN = 0;

        String[] COLUMNS = {
                Stacks.STACK_ID,
                Stacks.STACK_TITLE,
                Stacks.STACK_COUNT_CARDS
        };

        int ID = 0;
        int TITLE = 1;
        int COUNT_CARDS = 2;
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
