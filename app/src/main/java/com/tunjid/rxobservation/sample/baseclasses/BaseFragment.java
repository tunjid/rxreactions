package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v4.app.Fragment;

import com.tunjid.rxobservation.ObservationAction;
import com.tunjid.rxobservation.ObservationObserver;
import com.tunjid.rxobservation.ThrowableWrapper;
import com.tunjid.rxobservation.sample.observation.SampleMapper;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Fragment
 */

public abstract class BaseFragment extends Fragment
        implements ObservationAction<Object> {


    protected ObservationObserver observer = new ObservationObserver<>(new SampleMapper<>(), this);

    public ObservationObserver getObserver() {
        return observer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        observer.unsubscribeFromAll();
    }

    @Override
    public void proceed(String id, Object o) {

    }

    @Override
    public void resolve(String id, Object o) {

    }

    @Override
    public void onError(String id, ThrowableWrapper throwableWrapper) {

    }
}
