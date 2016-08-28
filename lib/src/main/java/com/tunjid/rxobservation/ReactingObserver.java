package com.tunjid.rxobservation;

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
 * An {@link Reactor} that observes events linked to a certain String id
 */

public class ReactingObserver<T, E> {

    private static final int TIMEOUT = 25;

    /**
     * A Mapping of all the observations this observer is yet to make
     */
    private HashMap<String, TrackingObserver> subscriptionMap = new HashMap<>();

    /**
     * A mapper used for deciding what happens after {@link Observer#onNext(Object)} is called
     */
    private ReactionMapper<T, E> mapper;

    /**
     * An action to complete post observation.
     */
    private Reactor<T, E> action;


    /**
     * @param mapper filters {@link T} to decide whether to {@link Reactor#onNext(String, Object)}
     * or {@link Reactor#onError(String, Object)} should be called
     */
    public ReactingObserver(ReactionMapper<T, E> mapper, Reactor<T, E> action) {
        this.mapper = mapper;
        this.action = action;
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
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final Subscription subscribeAsync(String id, Observable<T> observable) {
        return subscribe(id, observable, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    /**
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final Subscription subscribe(String id, Observable<T> observable,
                                        Scheduler subscribeOn, Scheduler observeOn) {

        TrackingObserver observer = subscriptionMap.get(id) != null
                ? subscriptionMap.get(id)
                : new TrackingObserver(id);

        observer.unsubscribe();

        return observer.subscribe(observable, subscribeOn, observeOn);
    }

    @SafeVarargs
    public static <T, E> Subscription shareObservableAsync(final String id, Observable<T> observable,
                                                           final ReactingObserver<T, E>... observers) {
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
    public static <T, E> Subscription shareObservable(final String id, Observable<T> observable,
                                                      Scheduler subscribeOn, Scheduler observeOn,
                                                      final ReactingObserver<T, E>... observers) {

        // Create ConnectableObservable to allow multiple observers
        final ConnectableObservable<T> connObs = observable.publish();

        // Subscribe all observers to the same observable.
        for (ReactingObserver<T, E> observer : observers) {
            if (observer != null) observer.subscribe(id, observable, subscribeOn, observeOn);
        }

        return connObs.connect();
    }

    /**
     * An {@link Observer} that maps each subcription to a String ID
     */
    private class TrackingObserver implements Observer<T> {

        String id;
        Subscription subscription;

        private TrackingObserver(String id) {
            this.id = id;
        }

        private Subscription subscribe(Observable<T> observable, Scheduler subscribeOn, Scheduler observeOn) {
            subscription = observable.timeout(TIMEOUT, TimeUnit.SECONDS)
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
            subscriptionMap.remove(id);
        }

        @Override
        public void onError(Throwable throwable) {
            subscriptionMap.remove(id);
            action.onError(id, mapper.getErrorObject(throwable));
        }

        @Override
        public void onNext(T observedObject) {
            E errorObject = mapper.checkForError(observedObject);

            if (errorObject == null) action.onNext(id, observedObject);
            else action.onError(id, errorObject);
        }
    }
}
