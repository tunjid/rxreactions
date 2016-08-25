package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v7.app.AppCompatActivity;

import com.tunjid.rxobservation.ObservationAction;
import com.tunjid.rxobservation.ObservationObserver;
import com.tunjid.rxobservation.ThrowableWrapper;
import com.tunjid.rxobservation.sample.observation.SampleMapper;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity
        implements ObservationAction {

    protected ObservationObserver observer = new ObservationObserver(new SampleMapper(), this);


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
