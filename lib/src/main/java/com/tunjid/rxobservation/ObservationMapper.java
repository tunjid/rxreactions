package com.tunjid.rxobservation;

import rx.Observable;

/**
 * Created by tj.dahunsi on 8/15/16.
 * Filters {@link Observation}s
 */
public interface ObservationMapper<T> {

    /**
     * Determines whether the {@link ObservationObserver} should call
     * This behavior is tightly coupled with {@link ObservationMapper#onNext(String, Object)}.
     * <p>
     * {@link ObservationObserver#proceed(Observation)} or
     * {@link ObservationObserver#resolve(Observation)} after
     * {@link ObservationObserver#onNext(Observation)}
     *
     * @return true to proceed, false to resolve.
     */
    boolean canProceed(Observation<T> observation);

    /**
     * Should there be an error in the rx chain, return this {@link Observation}.
     */
    Observation<T> onError(String id, Throwable thrown);


    /**
     * Called immediatley after {@link ObservationObserver#onNext(Observation)} and determines
     * the next action.
     * <p>
     * This behavior is tightly coupled with {@link ObservationMapper#canProceed(Observation)}.
     *
     * @param id the id this observation was going to be observed with
     * @return the appropriate {@link Observation} for
     * {@link ObservationObserver#proceed(Observation)} or
     * {@link ObservationObserver#resolve(Observation)}
     */
    <S> Observable<? extends Observation<T>> onNext(String id, S wrappedObject);


    class DefaultMapper<T> implements ObservationMapper<T> {


        @Override
        public boolean canProceed(Observation observation) {
            return !(observation.getObservedObject() instanceof Throwable);
        }

        @Override
        public Observation<T> onError(String id, Throwable thrown) {
            return null;
        }

        @Override
        public  <R>Observable<? extends Observation<T>> onNext(String id, R wrappedObject) {
            return Observable.just((Observation<T>) new DefaultObservation<>(id, wrappedObject));
        }

    }
}
