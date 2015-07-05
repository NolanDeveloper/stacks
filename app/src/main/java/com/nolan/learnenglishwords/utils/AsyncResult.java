package com.nolan.learnenglishwords.utils;

public class AsyncResult<ResultT> {
    public ResultT result;
    public Throwable exception;

    public AsyncResult(ResultT result) {
        this.result = result;
        this.exception = null;
    }

    public AsyncResult(Throwable exception) {
        this.result = null;
        this.exception = exception;
    }

    public boolean hasException() {
        return null != exception;
    }

    public void throwIfHasException() throws Throwable {
        if (hasException())
            throw exception;
    }
}
