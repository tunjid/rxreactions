package com.tunjid.rxobservation;

/**
 * Used to deliver a Successful {@link Observation}
 */
public final class DefaultObservation<T> implements Observation {

    private String id;
    private T observedObject;

    public DefaultObservation(String id, T observedObject) {
        this.id = id;
        this.observedObject = observedObject;
    }

    @Override
    public String getId() {
        return id;
    }

    public T getObservedObject() {
        return observedObject;
    }
}
