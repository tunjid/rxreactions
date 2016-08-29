package com.tunjid.rxreactions.sample.model;

/**
 * Created by tj.dahunsi on 8/18/16.
 * Simple user
 */

public class User extends BaseModel {
    String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
