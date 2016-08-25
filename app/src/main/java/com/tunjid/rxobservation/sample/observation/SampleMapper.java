package com.tunjid.rxobservation.sample.observation;

import com.tunjid.rxobservation.ObservationMapper;
import com.tunjid.rxobservation.ThrowableWrapper;

/**
 * Created by tj.dahunsi on 8/25/16.
 * A sample mapper
 */
public class SampleMapper<T> implements ObservationMapper<T> {


    @Override
    public boolean canProceed(Object observedObject) {
        return !(observedObject instanceof Throwable);
    }

    @Override
    public ThrowableWrapper getThrowableWrapper(Throwable thrown) {
        return new SampleThrowableWrapper(thrown);
    }


    public static class SampleThrowableWrapper extends ThrowableWrapper {

        public SampleThrowableWrapper(Throwable throwable) {
            super(throwable);
        }
    }

}
