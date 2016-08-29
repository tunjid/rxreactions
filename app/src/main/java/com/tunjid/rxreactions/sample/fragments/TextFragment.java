package com.tunjid.rxreactions.sample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tunjid.rxreactions.ReactingObserver;
import com.tunjid.rxreactions.Reactor;
import com.tunjid.rxreactions.sample.R;
import com.tunjid.rxreactions.sample.activity.MainActivity;
import com.tunjid.rxreactions.sample.model.BaseModel;
import com.tunjid.rxreactions.sample.model.Error;
import com.tunjid.rxreactions.sample.model.User;
import com.tunjid.rxreactions.sample.reaction.SampleMapper;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment
        implements Reactor<BaseModel, Error> {

    ReactingObserver<BaseModel, Error> observer =
            new ReactingObserver<>(new SampleMapper<BaseModel>(), this);

    TextView textView;

    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);
        textView = (TextView) rootView.findViewById(R.id.text);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        observer.unsubscribeFromAll();
    }

    @Override
    public void onNext(String id, BaseModel model) {
        switch (id) {
            case MainActivity.TEST_USER:
                User user = (User) model;
                textView.setText(user.getUsername());
                break;
            // Shouldn't happen unless observables and ids are mixed, in which case ¯\_(ツ)_/¯
            case MainActivity.TEST_INVALID_USER:
            case MainActivity.TEST_404:
                textView.setText(model.toString());
                break;
        }

    }

    @Override
    public void onError(String id, Error error) {
        switch (id) {
            case MainActivity.TEST_USER:
            case MainActivity.TEST_INVALID_USER:
            case MainActivity.TEST_404:
                textView.setText(error.getMessage());
                break;
        }
    }

    @Override
    public void onCompleted(String id) {
        // Nothing.
    }

    public ReactingObserver<BaseModel, Error> getObserver() {
        return observer;
    }
}
