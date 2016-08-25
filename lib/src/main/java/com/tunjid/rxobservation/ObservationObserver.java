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
 * An {@link Observer} that observes events
 */

public class ObservationObserver<T>
        implements
        ObservationAction<T> {

    private static final int TIMEOUT = BuildConfig.DEBUG ? 3 : 12;

    /**
     * A Mapping of all the observations this observer is yet to make
     */
    private HashMap<String, DiscreteObserver> subscriptionMap = new HashMap<>();

    /**
     * A mapper used for deciding what happens after {@link Observer#onNext(Object)} is called
     */
    private ObservationMapper<T> mapper;

    /**
     * An action to complete post observation.
     */
    private ObservationAction<T> action;


    /**
     * This constructor is used when the {@link ObservationObserver} wants to carry out post
     * {@link ObservationAction}s itself.
     *
     * @param mapper filters observations to decide whether to {@link ObservationAction#proceed(String, Object)}
     * or {@link ObservationAction#resolve(String, Object)}
     */
    public ObservationObserver(ObservationMapper<T> mapper) {
        this.mapper = mapper;
        this.action = this;
    }

    /**
     * This constructor is used when the {@link ObservationObserver} wants to delegate post
     * {@link ObservationAction}s another {@link ObservationAction}.
     *
     * @param mapper filters observations to decide whether to {@link ObservationAction#proceed(String, Object)}
     * or {@link ObservationAction#resolve(String, Object)}
     */
    public ObservationObserver(ObservationMapper<T> mapper, ObservationAction<T> action) {
        this.mapper = mapper;
        this.action = action;
    }

    public void proceed(String id, T t) {
        // Override as necessary
    }

    public void resolve(String id, T t) {
        // Override as necessary
    }

    @Override
    public void onError(String id, ThrowableWrapper throwableWrapper) {
        // Override as necessary
    }

    /**
     * Unsubscribe from the observation with the specified id
     */
    public void unsubscribe(String id) {
        DiscreteObserver observer = subscriptionMap.get(id);
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

        DiscreteObserver observer = subscriptionMap.get(id) != null
                ? subscriptionMap.get(id)
                : new DiscreteObserver(id);

        observer.unsubscribe();

        return observer.subscribe(observable, subscribeOn, observeOn);
    }

    public static <T> Subscription shareObservableAsync(final String id, Observable<T> observable,
                                                        final ObservationObserver... observers) {
        return shareObservable(id, observable, Schedulers.io(), AndroidSchedulers.mainThread(), observers);
    }

    /**
     * Shares an observable with all observers included, all observers will get all events of
     * the source observable
     *
     * @param id String id
     * @param observable The {@link Observable} to watch
     * @param observers All {@link ObservationObserver}s waiting to react
     */
    public static <T> Subscription shareObservable(final String id, Observable<T> observable,
                                                   Scheduler subscribeOn, Scheduler observeOn,
                                                   final ObservationObserver... observers) {

        // Create ConnectableObservable to allow multiple observers
        final ConnectableObservable<T> connObs = observable.publish();

        // Subscribe all observers to the same observable.
        for (ObservationObserver observer : observers) {
            if (observer != null) observer.subscribe(id, observable, subscribeOn, observeOn);
        }

        return connObs.connect();
    }


    class DiscreteObserver implements Observer<T> {

        String id;
        Subscription subscription;

        DiscreteObserver(String id) {
            this.id = id;
        }

        Subscription subscribe(Observable<T> observable, Scheduler subscribeOn, Scheduler observeOn) {
            subscription = observable.timeout(TIMEOUT, TimeUnit.SECONDS)
                    .subscribeOn(subscribeOn)
                    .observeOn(observeOn)
                    .subscribe(this);

            return subscription;
        }

        void unsubscribe() {
            if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        }

        @Override
        public void onCompleted() {
            subscriptionMap.remove(id);
        }

        @Override
        public void onError(Throwable throwable) {
            subscriptionMap.remove(id);
            action.onError(id, mapper.getThrowableWrapper(throwable));
        }

        @Override
        public void onNext(T observedObject) {
            if (mapper.canProceed(observedObject)) action.proceed(id, observedObject);
            else action.resolve(id, observedObject);
        }
    }


}
