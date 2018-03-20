package com.example.githubprofileviewer.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by arushi on 17/03/18.
 *
 * These utilities will be used to communicate with the network.
 */

public class NetworkUtils {

    private static Retrofit retrofit;

    /* API Timeouts - (in Seconds) */
    public static final int API_CONNECT_TIMEOUT = 10; // 10 sec
    public static final int API_WRITE_TIMEOUT = 240; // 4 min
    public static final int API_READ_TIMEOUT = 30; // 30 sec

    private final static String GITHUB_BASE_URL = "https://api.github.com/";
    final static String PATH_USERS = "users/";


    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(API_WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(GITHUB_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}