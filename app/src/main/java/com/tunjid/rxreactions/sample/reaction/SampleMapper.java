package com.tunjid.rxreactions.sample.reaction;

import com.tunjid.rxreactions.ReactionMapper;
import com.tunjid.rxreactions.sample.model.BaseModel;
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

        // If the endpoint returns a 200 code, but there's an error object in the response,
        // call onError.
        if (observedObject instanceof BaseModel) return ((BaseModel) observedObject).getError();

        // There's no error, return null so onNext can be called.
        return null;
    }

    @Override
    public Error getErrorObject(Throwable thrown) {
        return new Error(thrown);
    }


}
