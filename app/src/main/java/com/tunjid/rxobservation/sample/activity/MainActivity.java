package com.tunjid.rxobservation.sample.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tunjid.rxobservation.ObservationObserver;
import com.tunjid.rxobservation.sample.R;
import com.tunjid.rxobservation.sample.baseclasses.BaseActivity;
import com.tunjid.rxobservation.sample.fragments.TextFragment;
import com.tunjid.rxobservation.sample.rest.TestClient;

import static com.tunjid.rxobservation.sample.fragments.TextFragment.TEST_404;
import static com.tunjid.rxobservation.sample.fragments.TextFragment.TEST_ASYNC;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        TextFragment one = (TextFragment) getSupportFragmentManager().findFragmentById(R.id.one);
        TextFragment two = (TextFragment) getSupportFragmentManager().findFragmentById(R.id.two);
        TextFragment three = (TextFragment) getSupportFragmentManager().findFragmentById(R.id.three);
        TextFragment four = (TextFragment) getSupportFragmentManager().findFragmentById(R.id.four);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ObservationObserver.shareObservableAsync(TEST_ASYNC, TestClient.getTestApi().getUsers(),
                one.getObserver(), two.getObserver(),
                three.getObserver(), four.getObserver());

        ObservationObserver.shareObservableAsync(TEST_404, TestClient.getTestApi().get404(),
                one.getObserver(), two.getObserver(),
                three.getObserver(), four.getObserver());
    }
}
