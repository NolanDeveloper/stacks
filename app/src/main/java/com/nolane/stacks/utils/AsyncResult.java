package com.nolane.stacks.utils;

import android.content.AsyncTaskLoader;
import android.os.AsyncTask;

/**
 * This class is useful in case of using {@link AsyncTask} and {@link AsyncTaskLoader}.
 * <p>
 * You need use it if something can throw an exception during
 * {@link AsyncTaskLoader#loadInBackground()}. If everything was ok store here result of
 * {@link AsyncTaskLoader} otherwise store here exception to indicate failure and what is more
 * important specify failure.
 * <p>
 * You probably don't need to use this wrapper if your AsyncTask does not throw exceptions or
 * if error can be notified as null return value.
 * */
public class AsyncResult<T> {
    // Correct result.
    public final T result;
    // Exception which was thrown during async task.
    public final Throwable exception;

    /**
     * Constructs result of correctly worked async task.
     * @param result Result value of async task.
     */
    public AsyncResult(T result) {
        this.result = result;
        this.exception = null;
    }

    /**
     * Constructs result of incorrectly worked async task.
     * @param exception Throwable which was thrown during async task.
     */
    public AsyncResult(Throwable exception) {
        this.result = null;
        this.exception = exception;
    }

    /**
     * @return True if caught exception during async task and false otherwise.
     */
    public boolean hasException() {
        return null != exception;
    }

    /**
     * If this object caught exception during the background task throws this exception.
     * @throws Throwable Throwable which was caught during the async task.
     */
    public void throwIfHasException() throws Throwable {
        if (hasException()) throw exception;
    }
}
