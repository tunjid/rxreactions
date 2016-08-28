package com.tunjid.rxobservation.sample.reaction;

import com.tunjid.rxobservation.ReactionMapper;
import com.tunjid.rxobservation.sample.model.Error;

/**
 * Created by tj.dahunsi on 8/25/16.
 * A sample mapper
 */
public class SampleMapper<T> implements ReactionMapper<T, Error> {


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
