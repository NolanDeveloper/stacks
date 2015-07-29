package com.nolane.stacks.utils;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.nolane.stacks.provider.CursorWrapper;

/**
 * Base class for recycler view adapters which use cursors.
 */
public abstract class RecyclerCursorWrapperAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    // Query which this adapter wraps. This also available in subclasses.
    @Nullable
    protected CursorWrapper<T> query;

    public RecyclerCursorWrapperAdapter(@Nullable CursorWrapper<T> query) {
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

    public void setCursorWrapper(@Nullable CursorWrapper<T> query) {
        if (this.query == query)
            return;
        if (null != this.query) {
            this.query.close();
        }
        this.query = query;
        notifyDataSetChanged();
    }
}
