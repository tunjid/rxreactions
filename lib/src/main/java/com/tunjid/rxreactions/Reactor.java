package com.tunjid.rxreactions;

/**
 * Created by tj.dahunsi on 8/16/16.
 * The actions to complete after {@link rx.Observer#onNext(Object)}
 *
 * This method mimic the methods of {@link rx.Observer}, with the added convinience of adding
 * an String id to identify the source.
 */
public interface Reactor<T, E> {

    void onNext(String id, T observedObject);

    void onError(String id, E errorObject);

    void onCompleted(String id);
}
