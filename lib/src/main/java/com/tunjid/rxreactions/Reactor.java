package com.tunjid.rxreactions;

/**
 * Created by tj.dahunsi on 8/16/16.
 * The actions to complete after {@link rx.Observer#onNext(Object)}
 */
public interface Reactor<T, E> {

    void onNext(String id, T observedType);

    void onError(String id, E errorType);
}
