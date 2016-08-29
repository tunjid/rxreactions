package com.tunjid.rxreactions.sample.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Request;
import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by tj.dahunsi on 8/27/16.
 * Errors with messages
 */

public class Error {

    String message;
    Throwable throwable;

    /**
     * <p>The {@link okhttp3.ResponseBody} object in the {@link HttpException} can only be consumed
     * once. For API calls that have more  than one observer this creates a race condition on who
     * can consume the error response body first.</p>
     * <p/>
     * <p>To avoid this a LRUCache is used. This cache only keeps a fixed amonunt of responses
     * in memory, and regardless of which observer consumes the error first, the cache ensures
     * they have the same Error object.</p>
     */
    private static final Map<Request, Error> cachedResponses = createLRUMap(6);

    public Error(Throwable throwable) {
        this.throwable = throwable;

        Log.d("ERROR", "Observable error", throwable);

        if (throwable instanceof IOException) {

            if (throwable instanceof ConnectException) {
                message = "Could not establish a connection";
            }
            if (throwable instanceof SocketTimeoutException)
                message = "Request timed out";

            else if (throwable instanceof MalformedJsonException) {
                message = "Malformed JSON";
            }
            else {
                message = "No internet connection";
            }
        }
        else if (throwable instanceof HttpException) {

            HttpException httpException = (HttpException) throwable;
            Request request = httpException.response().raw().request();

            if (!cachedResponses.containsKey(request)) {

                try {
                    String json = httpException.response().errorBody().string();
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    Error error = gson.fromJson(json, Error.class);

                    this.message = error.getMessage();

                    cachedResponses.put(request, error);

                    Log.d("ERROR", "ERROR WAS CREATED FROM HTTPEXCEPTION");

                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ERROR", "ERROR WAS NOT CREATED");
                }
            }
            else {
                Error error = cachedResponses.get(request);
                this.message = error.getMessage();

                Log.d("ERROR", "ERROR WAS RETRIEVED FROM CACHE");
            }
        }

        if (this.message == null) {
            this.message = throwable.getMessage() != null
                    ? throwable.getMessage()
                    : "Sorry, there was an error";
        }
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public static <K, V> Map<K, V> createLRUMap(final int maxEntries) {
        return new LinkedHashMap<K, V>(maxEntries * 10 / 7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxEntries;
            }
        };
    }
}
