package com.tunjid.rxobservation.sample.reaction;

import android.util.Log;

import com.tunjid.rxobservation.ReactionMapper;
import com.tunjid.rxobservation.sample.model.Error;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by tj.dahunsi on 8/25/16.
 * A sample mapper
 */
public class SampleMapper implements ReactionMapper<Object, Error> {


    @Override
    public Error getErrorResponse(Object observedObject) {
        // no error to return here
        return null;
    }

    @Override
    public Error getErrorResponse(Throwable thrown) {

        Log.d("ERROR", "Observable error", thrown);

        String message = "Sorry, there was an error";

        if (thrown instanceof IOException) {

            if (thrown instanceof ConnectException)
                message = "Could not establish a connection";

            else if (thrown instanceof SocketTimeoutException)
                message = "Request timed out";

            else
                message = "No internet connection";
        }
        else if (thrown instanceof HttpException) {

            HttpException httpException = (HttpException) thrown;

            try {
                message = httpException.response().errorBody().string();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Error(message);
    }


}
