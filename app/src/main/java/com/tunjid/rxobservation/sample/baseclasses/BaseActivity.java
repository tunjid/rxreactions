package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v7.app.AppCompatActivity;

import com.tunjid.rxobservation.ReactingObserver;
import com.tunjid.rxobservation.Reaction;
import com.tunjid.rxobservation.sample.model.Error;
import com.tunjid.rxobservation.sample.reaction.SampleMapper;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Activity
 */

public abstract class BaseActivity extends AppCompatActivity
        implements Reaction<Object, Error> {

    protected ReactingObserver observer = new ReactingObserver<>(new SampleMapper(), this);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        observer.unsubscribeFromAll();
    }

    @Override
    public void onNext(String id, Object o) {

    }

    @Override
    public void onError(String id, Error error) {

    }
}
