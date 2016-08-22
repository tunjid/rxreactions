package com.tunjid.rxobservation.sample.baseclasses;

import android.support.v4.app.Fragment;

import com.tunjid.rxobservation.Observation;
import com.tunjid.rxobservation.ObservationAction;
import com.tunjid.rxobservation.ObservationMapper;
import com.tunjid.rxobservation.ObservationObserver;

import lombok.Getter;

/**
 * Created by tj.dahunsi on 8/17/16.
 * Base Fragment
 */

public abstract class BaseFragment extends Fragment
        implements ObservationAction<Object> {

    @Getter
    protected ObservationObserver observer = new ObservationObserver<>(new ObservationMapper.DefaultMapper(), this);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        observer.unsubscribeFromAll();
    }

    @Override
    public void proceed(Observation<?> observation) {

    }

    @Override
    public void resolve(Observation<?> observation) {

    }
}
