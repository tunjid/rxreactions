package com.tunjid.rxreactions;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by tj.dahunsi on 8/15/16.
 * An object that observes events linked to a certain String id
 */

public class ReactingObserver<T, E> {

    int timeout;
    TimeUnit timeUnit;

    /**
     * A Mapping of String ids to Observers
     */
    private HashMap<String, TrackingObserver> subscriptionMap = new HashMap<>();

    /**
     * A mapper used for deciding what happens after {@link Observer#onNext(Object)} is called
     */
    private ReactionMapper<T, E> mapper;

    /**
     * A reactor to react to the events produced.
     */
    private Reactor<T, E> reactor;


    /**
     * @param mapper filters {@link T} to decide whether to {@link Reactor#onNext(String, Object)}
     * or {@link Reactor#onError(String, Object)} should be called.
     * @param reactor ab object that reactsto the emissions of the {@link Observable}s subscribed to
     */
    public ReactingObserver(ReactionMapper<T, E> mapper, Reactor<T, E> reactor) {
        this.mapper = mapper;
        this.reactor = reactor;
    }

    /**
     * Set the timeout for the observables observed
     *
     * @param timeout the time out duration, or 0 to ignore
     * @param timeUnit the time unit for the duration or null to ignore
     */
    public void setTimeout(int timeout, TimeUnit timeUnit) {
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    /**
     * Unsubscribe from the observable with the specified id
     */
    public void unsubscribe(String id) {
        TrackingObserver observer = subscriptionMap.get(id);
        if (observer != null) observer.unsubscribe();
    }

    /**
     * Unsubscribes from all observables
     */
    public void unsubscribeFromAll() {
        for (String id : subscriptionMap.keySet()) unsubscribe(id);
    }

    /**
     * Unsubscribes from all events, nulls the {@link ReactionMapper} and {@link Reactor},
     * and renders this {@link ReactingObserver} useless.
     */
    public void clear() {
        unsubscribeFromAll();
        reactor = null;
        mapper = null;
    }


    /**
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final Subscription subscribeAsync(String id, Observable<? extends T> observable) {
        return subscribe(id, observable, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    /**
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final Subscription subscribe(String id, Observable<? extends T> observable,
                                        Scheduler subscribeOn, Scheduler observeOn) {

        if (reactor == null) throw new IllegalArgumentException("Reactor can not be null");
        if (mapper == null) throw new IllegalArgumentException("Mapper can not be null");

        TrackingObserver observer = subscriptionMap.get(id) != null
                ? subscriptionMap.get(id)
                : new TrackingObserver(id);

        // Unsubscribe if this observer was subscribed to something before to allow resuse.
        observer.unsubscribe();

        return observer.subscribe(observable, subscribeOn, observeOn);
    }

    @SafeVarargs
    public static <T, E> Subscription shareObservableAsync(final String id, Observable<? extends T> observable,
                                                           final ReactingObserver<? extends T, E>... observers) {
        return shareObservable(id, observable, Schedulers.io(), AndroidSchedulers.mainThread(), observers);
    }

    /**
     * Shares an observable with all observers included, all observers will get all events of
     * the source observable
     *
     * @param id String id
     * @param observable The {@link Observable} to watch
     * @param observers All {@link ReactingObserver}s waiting to react
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    // weird generic comple time error if upper bound is specified in for loop
    public static <T, E> Subscription shareObservable(String id, Observable<? extends T> observable,
                                                      Scheduler subscribeOn, Scheduler observeOn,
                                                      ReactingObserver<? extends T, E>... observers) {

        // Create ConnectableObservable to allow multiple observers
        final ConnectableObservable<? extends T> connObs = observable.publish();

        // Subscribe all observers to the same observable.

        for (ReactingObserver observer : observers) {
            if (observer != null) {
                observer.subscribe(id, connObs, subscribeOn, observeOn);
            }
        }

        return connObs.connect();
    }

    /**
     * An {@link Observer} that maps each subcription to a String ID
     */
    private class TrackingObserver implements Observer<T> {

        private String id;
        private Subscription subscription;

        private TrackingObserver(String id) {
            this.id = id;
        }

        private Subscription subscribe(Observable<? extends T> observable,
                                       Scheduler subscribeOn, Scheduler observeOn) {

            if (timeout != 0 && timeUnit != null)
                observable = observable.timeout(timeout, timeUnit);

            subscription = observable
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)
                    .subscribe(this);

            return subscription;
        }

        private void unsubscribe() {
            if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        }

        @Override
        public void onCompleted() {
            // remove this observer from the map
            subscriptionMap.remove(id);
            reactor.onCompleted(id);
        }

        @Override
        public void onError(Throwable throwable) {
            // remove this observer from the map
            subscriptionMap.remove(id);

            if (reactor != null && mapper != null)
                reactor.onError(id, mapper.getErrorObject(throwable));
        }

        @Override
        public void onNext(T observedObject) {
            if (reactor != null && mapper != null) {
                E errorObject = mapper.checkForError(observedObject);

                if (errorObject == null) reactor.onNext(id, observedObject);
                else reactor.onError(id, errorObject);
            }
        }
    }
}
