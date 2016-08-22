package com.tunjid.rxobservation;

/**
 * Created by tj.dahunsi on 8/16/16.
 * The actions to complete after {@link ObservationObserver#onNext(Observation)}
 */
public interface ObservationAction<T> {

    void proceed(Observation<? extends T> observation);

    void resolve(Observation<? extends T> observation);
}
