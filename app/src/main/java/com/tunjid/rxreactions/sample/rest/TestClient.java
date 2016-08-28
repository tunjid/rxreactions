package com.tunjid.rxreactions.sample.rest;

import com.google.gson.Gson;
import com.tunjid.rxreactions.sample.model.User;

import java.io.IOException;
import java.net.URI;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.tunjid.rxreactions.sample.rest.TestClient.TestApi.ENDPOINT;

/**
 * Created by tj.dahunsi on 8/18/16.
 * Test client
 */

public class TestClient {

    private static final TestApi testApi = new Retrofit.Builder()
            .client(new OkHttpClient.Builder().addInterceptor(new MockInterceptor()).build())
            .baseUrl(ENDPOINT)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(GsonConverterFactory.create(new Gson()))
            .build()
            .create(TestApi.class);

    public static TestApi getTestApi() {
        return testApi;
    }

    public interface TestApi {

        String ENDPOINT = "https://test.com/";

        @GET("user")
        Observable<User> getUser();

        @GET("invalid_user")
        Observable<User> getInvalidUser();

        @GET("users")
        Observable<User> get404();
    }

    static class MockInterceptor implements Interceptor {

        // FAKE RESPONSES.
        private final static String USER = "{\"username\":\"Lad\"}";
        private final static String INVALID_USER = "{\"error\":{\"message\":\"User does not exist\"}}";
        private final static String ERROR = "{\"message\":\"Not a valid endpoint\"}";

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response;

            String responseString;

            // Get Request URI.
            final URI uri = chain.request().url().uri();

            // Get request path.
            final String path = uri.getPath();

            switch (path) {
                case "/user":
                    responseString = USER;
                    break;
                case "/invalid_user":
                    responseString = INVALID_USER;
                    break;
                default:
                    responseString = ERROR;
                    break;
            }

            response = new Response.Builder()
                    .code(responseString.equals(ERROR) ? 404 : 200)
                    .message(responseString)
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_0)
                    .body(ResponseBody.create(MediaType.parse("application/json"), responseString.getBytes()))
                    .addHeader("content-type", "application/json")
                    .build();

            return response;
        }
    }
}
