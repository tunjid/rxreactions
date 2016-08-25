package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/16/16.
 * The actions to complete after {@link rx.Observer#onNext(Object)}
 */
public interface ObservationAction<T> {

    void proceed(String id, T t);

    void resolve(String id, T t);

    void onError(String id,  ThrowableWrapper throwableWrapper);
}
