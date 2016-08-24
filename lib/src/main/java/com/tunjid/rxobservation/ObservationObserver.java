package com.tunjid.rxobservation;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

/**
 * Created by tj.dahunsi on 8/15/16.
 * An {@link Observer} that observes {@link Observation}s
 */

public class ObservationObserver<T>
        implements
        ObservationAction<T>,
        Observer<Observation<T>> {

    private static final int TIMEOUT = BuildConfig.DEBUG ? 3 : 12;

    /**
     * A Mapping of all the observations this observer is yet to make
     */
    private ReactiveSubscriptionMap<T> subscriptionMap = new ReactiveSubscriptionMap<>();

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
     * {@link Observation} actions itself.
     *
     * @param mapper filters observations to decide whether to {@link ObservationAction#proceed(Observation)}
     * or {@link ObservationAction#resolve(Observation)}
     */
    public ObservationObserver(ObservationMapper<T> mapper) {
        this.mapper = mapper;
        this.action = this;
    }

    /**
     * This constructor is used when the {@link ObservationObserver} wants to delegate post
     * {@link Observation} actions another {@link ObservationAction}.
     *
     * @param mapper filters observations to decide whether to {@link ObservationAction#proceed(Observation)}
     * or {@link ObservationAction#resolve(Observation)}
     */
    public ObservationObserver(ObservationMapper<T> mapper, ObservationAction<T> action) {
        this.mapper = mapper;
        this.action = action;
    }

    public void proceed(Observation observation) {

    }

    public void resolve(Observation observation) {

    }

    @Override
    public void onNext(Observation<T> observation) {
        if (action == null) throw new NullPointerException("Action cannot be null");

        if (mapper.canProceed(observation)) action.proceed(observation);
        else action.resolve(observation);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onCompleted() {

    }

    /**
     * Unsubscribe from the observation with the specified id
     */
    public void unsubscribe(String id) {
        subscriptionMap.unsubscribe(id);
    }

    /**
     * Unsubscribes from all observations
     */
    public void unsubscribeFromAll() {
        subscriptionMap.unsubscribeFromAll();
    }


    /**
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final <S> Subscription subscribeAsync(String id, Observable<S> observable) {
        return subscribe(id, observable, Schedulers.io(), AndroidSchedulers.mainThread());
    }

    /**
     * Subcribes to this observable event asynchronously
     *
     * @param id the id used to identify the observable when it completes
     */
    public final <S> Subscription subscribe(String id, Observable<S> observable,
                                            Scheduler subscribeOn, Scheduler observeOn) {

        ConnectableObservable<Observation<T>> observationObservable = create(id, observable)
                .timeout(TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(subscribeOn)
                .observeOn(observeOn)
                .publish();

        observationObservable.subscribe(this);
        subscriptionMap.subscribe(id, observationObservable);

        Subscription subscription = observationObservable.connect();
        subscriptionMap.put(id, subscription);

        return subscription;
    }

    public static <T> Subscription shareObservableAsync(final String id, Observable<T> observable,
                                                        final ObservationObserver... observers) {
        return shareObservable(id, observable, Schedulers.io(), AndroidSchedulers.mainThread(), observers);
    }

    /**
     * Shares an observable with all observers included, all observers will get all events of
     * the source observable
     *
     * @param id String id as specified in {@link Observation#getId()}
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

    /**
     * Used to observe emmisions of {@link Observation}
     *
     * @param id The id used to identify the kind of this observable
     * @param observable The observable that will emit an object defined in the api call
     * @return An {@link Observation} containing the object produced from the asynchronous event
     */
    private <S> Observable<Observation<T>> create(final String id, final Observable<S> observable) {

        return observable
                .flatMap(new Func1<S, Observable<? extends Observation<T>>>() {
                    @Override
                    public Observable<? extends Observation<T>> call(S observedOjbect) {
                        return mapper.onNext(id, observedOjbect);
                    }
                })
                .onErrorReturn(new Func1<Throwable, Observation<T>>() {
                    @Override
                    public Observation<T> call(Throwable thrown) {
                        return mapper.onError(id, thrown);
                    }
                });
    }

}
