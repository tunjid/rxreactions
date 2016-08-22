package com.tunjid.rxobservation.sample.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tunjid.rxobservation.Observation;
import com.tunjid.rxobservation.sample.R;
import com.tunjid.rxobservation.sample.baseclasses.BaseFragment;
import com.tunjid.rxobservation.sample.model.User;

import java.util.ArrayList;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends BaseFragment {

    public static final String TEST_ASYNC = "TEST_ASYNC";
    public static final String TEST_SYNC = "TEST_SYNC";
    public static final String TEST_WEB = "TEST_WEB";
    public static final String TEST_404 = "TEST_404";

    long count = -1;
    TextView textView;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);
        textView = (TextView) rootView.findViewById(R.id.text);

        return rootView;
    }

    @Override
    public void proceed(Observation<Object> observation) {
        switch (observation.getId()) {
            case TEST_ASYNC:
                String string1 = String.valueOf((long) observation.getObservedObject());
                textView.setText(string1);
                break;
            case TEST_SYNC:
                String string2 = String.valueOf((long) observation.getObservedObject());
                textView.setText(string2);

                textView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        observer.subscribe(TEST_SYNC, Observable.just(++count));
                    }
                }, 1000);
                break;
            case TEST_WEB:
                ArrayList<User> users = (ArrayList<User>) observation.getObservedObject();
                textView.setText(users.get(0).getUsername());
                break;
        }

    }

    @Override
    public void resolve(Observation<Object> observation) {
        switch (observation.getId()) {
            case TEST_404:
                if (observation.getObservedObject() instanceof Throwable) {
                    Throwable throwable = (Throwable) observation.getObservedObject();
                    throwable.printStackTrace();

                    if (throwable.getMessage() != null && getView() != null) {
                        Snackbar.make(getView(), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }
}
