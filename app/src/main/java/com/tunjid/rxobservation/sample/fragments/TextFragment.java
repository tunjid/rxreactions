package com.tunjid.rxobservation.sample.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tunjid.rxobservation.ThrowableWrapper;
import com.tunjid.rxobservation.sample.R;
import com.tunjid.rxobservation.sample.baseclasses.BaseFragment;
import com.tunjid.rxobservation.sample.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends BaseFragment {

    public static final String TEST_ASYNC = "TEST_ASYNC";
    public static final String TEST_WEB = "TEST_WEB";
    public static final String TEST_404 = "TEST_404";

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
    public void proceed(String id, Object object) {
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
    public void resolve(String id, Object object) {

    }

    @Override
    public void onError(String id, ThrowableWrapper throwableWrapper) {
        super.onError(id, throwableWrapper);

        switch (id) {
            case TEST_404:
                Throwable throwable = throwableWrapper.getThrowable();
                throwable.printStackTrace();

                if (throwable.getMessage() != null && getView() != null) {
                    Snackbar.make(getView(), throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
