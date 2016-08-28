package com.tunjid.rxreactions.sample.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tunjid.rxreactions.ReactingObserver;
import com.tunjid.rxreactions.Reactor;
import com.tunjid.rxreactions.sample.R;
import com.tunjid.rxreactions.sample.model.Error;
import com.tunjid.rxreactions.sample.model.User;
import com.tunjid.rxreactions.sample.reaction.SampleMapper;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment
        implements Reactor<Object, Error> {

    public static final String TEST_ASYNC = "TEST_ASYNC";
    public static final String TEST_WEB = "TEST_WEB";
    public static final String TEST_404 = "TEST_404";

    ReactingObserver observer = new ReactingObserver<>(new SampleMapper<>(), this);
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
    public void onNext(String id, Object object) {
        switch (id) {
            case TEST_ASYNC:
                String string1 = String.valueOf((long) object);
                textView.setText(string1);
                break;

            case TEST_WEB:
                ArrayList<User> users = (ArrayList<User>) object;
                textView.setText(users.get(0).getUsername());
                break;
        }

    }

    @Override
    public void onError(String id, Error error) {

        switch (id) {
            case TEST_404:
                if (getView() != null)
                    Snackbar.make(getView(), error.getMessage(), Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    public ReactingObserver getObserver() {
        return observer;
    }
}
