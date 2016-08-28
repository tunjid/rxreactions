package com.tunjid.rxreactions.sample.model;

import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by tj.dahunsi on 8/27/16.
 * Errors with messages
 */

public class Error {

    Throwable throwable;
    String message;

    public Error(Throwable throwable) {
        this.throwable = throwable;

        Log.d("ERROR", "Observable error", throwable);

        if (throwable instanceof IOException) {

            if (throwable instanceof ConnectException)
                message = "Could not establish a connection";

            else if (throwable instanceof SocketTimeoutException)
                message = "Request timed out";

            else
                message = "No internet connection";
        }
        else if (throwable instanceof HttpException) {

            HttpException httpException = (HttpException) throwable;

            try {
                message = httpException.response().errorBody().string();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.message = throwable.getMessage() != null
                ? throwable.getMessage()
                : "Sorry, there was an error";
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
