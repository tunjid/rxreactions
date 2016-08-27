package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v4.app.Fragment;

import com.tunjid.rxobservation.ReactingObserver;
import com.tunjid.rxobservation.Reaction;
import com.tunjid.rxobservation.sample.model.Error;
import com.tunjid.rxobservation.sample.reaction.SampleMapper;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Fragment
 */

public abstract class BaseFragment extends Fragment
        implements Reaction<Object, Error> {


    protected ReactingObserver observer = new ReactingObserver<>(new SampleMapper(), this);

    public ReactingObserver getObserver() {
        return observer;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        observer.unsubscribeFromAll();
    }

    @Override
    public void onNext(String id, Object o) {

    }

    @Override
    public void onError(String id, Error error) {

    }
}
