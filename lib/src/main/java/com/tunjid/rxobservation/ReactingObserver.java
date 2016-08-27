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
 * An {@link Reaction} that observes events linked to a certain String id
 */

public class ReactingObserver<T, E>
        implements
        Reaction<T, E> {

    private static final int TIMEOUT = BuildConfig.DEBUG ? 3 : 12;

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
    private Reaction<T, E> action;


    /**
     * This constructor is used when the {@link ReactingObserver} wants to carry out post
     * {@link Reaction}s itself.
     *
     * @param mapper filters observations to decide whether to {@link Reaction#onNext(String, Object)}
     * or {@link Reaction#onError(String, Object)}
     */
    public ReactingObserver(ReactionMapper<T, E> mapper) {
        this.mapper = mapper;
        this.action = this;
    }

    /**
     * This constructor is used when the {@link ReactingObserver} wants to delegate post
     * {@link Reaction}s another {@link Reaction}.
     *
     * @param mapper filters observations to decide whether to {@link Reaction#onNext(String, Object)}
     * or {@link Reaction#onError(String, Object)}
     */
    public ReactingObserver(ReactionMapper<T, E> mapper, Reaction<T, E> action) {
        this.mapper = mapper;
        this.action = action;
    }

    public void onNext(String id, T observedType) {
        // Override as necessary
    }

    @Override
    public void onError(String id, E errorType) {
        // Override as necessary
    }

    /**
     * Unsubscribe from the observation with the specified id
     */
    public void unsubscribe(String id) {
        TrackingObserver observer = subscriptionMap.get(id);
        if (observer != null) observer.unsubscribe();
    }

    /**
     * Unsubscribes from all observations
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
            action.onError(id, mapper.getErrorResponse(throwable));
        }

        @Override
        public void onNext(T observedObject) {
            if (mapper.getErrorResponse(observedObject) == null) action.onNext(id, observedObject);
            else action.onError(id, mapper.getErrorResponse(observedObject));
        }
    }
}
