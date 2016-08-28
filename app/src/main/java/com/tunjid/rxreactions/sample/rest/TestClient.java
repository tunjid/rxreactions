package com.tunjid.rxreactions.sample.rest;

import com.google.gson.Gson;
import com.tunjid.rxreactions.sample.model.User;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by tj.dahunsi on 8/18/16.
 * Test client
 */

public class TestClient {

    private static final Retrofit restAdapter = new Retrofit.Builder()
            .client(new OkHttpClient())
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build();


    private static final TestApi testApi = restAdapter.create(TestApi.class);

    public static TestApi getTestApi() {
        return testApi;
    }

    public interface TestApi {
        @GET("users")
        Observable<ArrayList<User>> getUsers();

        @GET("user")
        Observable<User> get404();
    }
}
