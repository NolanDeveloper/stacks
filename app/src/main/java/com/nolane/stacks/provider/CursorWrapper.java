package com.nolane.stacks.provider;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CursorWrapper<T> {
    public interface ModelFactory<T> {
        void prepare(@NonNull Cursor c);
        @NonNull
        T wrapRow(@NonNull Cursor c);
    }

    @NonNull
    private Cursor cursor;
    @NonNull
    private ModelFactory<T> factory;
    @Nullable
    private T cachedRow;

    private void checkNotClosed() {
        if (cursor.isClosed()) {
            throw new IllegalStateException("Cursor was closed.");
        }
    }

    public CursorWrapper(@NonNull Cursor cursor, @NonNull ModelFactory<T> factory) {
        this.cursor = cursor;
        this.factory = factory;
        checkNotClosed();
        factory.prepare(cursor);
    }

    public int getCount() {
        checkNotClosed();
        return cursor.getCount();
    }

    public boolean moveToNext() {
        checkNotClosed();
        cachedRow = null;
        return cursor.moveToNext();
    }

    @NonNull
    public T get() {
        checkNotClosed();
        if (null == cachedRow) cachedRow = factory.wrapRow(cursor);
        return cachedRow;
    }

    @NonNull
    public T getAtPosition(int position) {
        checkNotClosed();
        cursor.moveToPosition(position);
        cachedRow = factory.wrapRow(cursor);
        return cachedRow;
    }

    public void close() {
        checkNotClosed();
        cursor.close();
    }
}
