package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/15/16.
 * Maps {@link ObservationAction}
 */
public interface ObservationMapper<T> {

    /**
     * Determines whether the {@link ObservationObserver} should call
     * {@link ObservationObserver#proceed(String, Object)}  or
     * {@link ObservationObserver#resolve(String, Object)}  after
     * {@link rx.Observer#onNext(Object)}
     *
     * @return true to proceed, false to resolve.
     */
    boolean canProceed(T observedObject);

    /**
     * Should there be an error in the rx chain, return the {@link Throwable} wrapped in this class
     * for more options
     */
    ThrowableWrapper getThrowableWrapper(Throwable thrown);


}
