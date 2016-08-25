package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/25/16.
 */

public abstract class ThrowableWrapper {
    Throwable throwable;

    public ThrowableWrapper(Throwable throwable){
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
