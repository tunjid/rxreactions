package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/15/16.
 * Maps {@link Reaction}
 */
public interface ReactionMapper<T, E> {

    /**
     * Determines whether the {@link ReactingObserver} should call
     * {@link ReactingObserver#onNext(String, Object)}  or
     * {@link ReactingObserver#onError(String, Object)} after
     * {@link rx.Observer#onNext(Object)}
     *
     * @return a value to call {@link ReactingObserver#onError(String, Object)},
     * {@code null} to call {@link ReactingObserver#onNext(String, Object)}.
     */

    E getErrorResponse(T observedObject);

    /**
     * Should there be an error in the rx chain, return the {@link E} object to react to
     */
    E getErrorResponse(Throwable thrown);
}
