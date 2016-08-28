package com.tunjid.rxreactions;

/**
 * Created by tj.dahunsi on 8/15/16.
 * Maps {@link Reactor}
 */
public interface ReactionMapper<T, E> {

    /**
     * Determines whether the {@link ReactingObserver} should call
     * {@link Reactor#onNext(String, Object)}  or
     * {@link Reactor#onError(String, Object)} after
     * {@link rx.Observer#onNext(Object)}
     *
     * @return a value {@link E} to call {@link Reactor#onError(String, Object)},
     * {@code null} to call {@link Reactor#onNext(String, Object)}.
     */

    E checkForError(T observedObject);

    /**
     * Should there be an error in the rx chain, return the {@link E} object to react to
     */
    E getErrorObject(Throwable thrown);
}
