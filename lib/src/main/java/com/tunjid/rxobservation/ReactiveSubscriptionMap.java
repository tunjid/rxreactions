package com.tunjid.rxobservation;

import java.util.HashMap;

import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

/**
 * Created by tj.dahunsi on 8/23/16.
 * A Mapping of subscriptions
 */
class ReactiveSubscriptionMap<T>
        implements
        Observer<Observation<T>> {

    private final HashMap<String, Subscription> subscriptionMap = new HashMap<>();


    void subscribe(final String id, ConnectableObservable<Observation<T>> observationObservable) {

        // Create an AsyncSubject to cache responses till the actual observable completes
        AsyncSubject<Observation<T>> completionSubject = AsyncSubject.create();

        // Use the AsyncSubject as an observer and subscribe it to the connectable observable
        observationObservable.subscribe(completionSubject);

        // Use the AsyncSubject as an observable and subscribe this ReactiveMap to it.
        completionSubject.onErrorReturn(getErrorFunction(id))
                .subscribeOn(Schedulers.io())
                .subscribe(this);
    }

    void unsubscribe(String id) {
        Subscription subscription = subscriptionMap.get(id);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
    }

    void unsubscribeFromAll() {
        for (String id : subscriptionMap.keySet()) unsubscribe(id);
    }

    void put(String key, Subscription value) {
        subscriptionMap.put(key, value);
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(Observation<T> observation) {
        subscriptionMap.remove(observation.getId());
    }

    private Func1<Throwable, Observation<T>> getErrorFunction(final String id) {
        return new Func1<Throwable, Observation<T>>() {
            @Override
            public Observation<T> call(Throwable throwable) {
                return new Observation<T>() {
                    @Override
                    public String getId() {
                        return id;
                    }

                    @Override
                    public T getObservedObject() {
                        return null;
                    }
                };
            }
        };
    }
}
