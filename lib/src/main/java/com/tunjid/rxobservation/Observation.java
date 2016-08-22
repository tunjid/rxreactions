package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/15/16.
 * An interface objects implement to be observed by an {@link ObservationObserver}
 */

public interface Observation<T> {

    /**
     * The id used to identify the {@link Observation}
     */
    String getId();

    /**
     * Gets the observed object from an {@link Observation}
     * class. If the wrapped object is a different type than what you try to assign it to
     * a {@link ClassCastException} will be thrown and a the
     * {@link ObservationObserver#onError(Throwable)} method will be called
     *
     * @return The observed object
     */
    T getObservedObject();
}
