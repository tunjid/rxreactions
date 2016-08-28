package com.tunjid.rxreactions.sample.reaction;

import com.tunjid.rxreactions.ReactionMapper;
import com.tunjid.rxreactions.sample.model.Error;

import java.util.concurrent.TimeUnit;

/**
 * Created by tj.dahunsi on 8/25/16.
 * A sample mapper
 */
public class SampleMapper<T> implements ReactionMapper<T, Error> {

    public static final int DEFAULT_TIME_OUT = 12;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    @Override
    public Error checkForError(T observedObject) {
        // no error to return here
        return null;
    }

    @Override
    public Error getErrorObject(Throwable thrown) {
        return new Error(thrown);
    }


}
