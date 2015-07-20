package com.nolane.stacks.utils;

import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Base class for recycler view adapters which use cursors.
 */
public abstract class RecyclerCursorAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    // Query which this adapter wraps. This also available in subclasses.
    @Nullable
    protected Cursor query;

    public RecyclerCursorAdapter(@Nullable Cursor query) {
        super();
        this.query = query;
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
