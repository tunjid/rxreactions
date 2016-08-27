package com.tunjid.rxobservation.sample.model;

/**
 * Created by tj.dahunsi on 8/27/16.
 * Errors with essages
 */

public class Error {

    String message;

    public Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
