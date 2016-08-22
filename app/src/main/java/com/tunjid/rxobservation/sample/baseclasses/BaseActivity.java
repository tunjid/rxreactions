package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v7.app.AppCompatActivity;

import com.tunjid.rxobservation.Observation;
import com.tunjid.rxobservation.ObservationAction;
import com.tunjid.rxobservation.ObservationMapper;
import com.tunjid.rxobservation.ObservationObserver;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity
        implements ObservationAction {

    protected ObservationObserver observer = new ObservationObserver(new ObservationMapper.DefaultMapper(), this);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        observer.unsubscribeFromAll();
    }

    @Override
    public void proceed(Observation observation) {
        // Overidden in subclasses
    }

    @Override
    public void resolve(Observation observation) {
        // Overidden in subclasses
    }
}
