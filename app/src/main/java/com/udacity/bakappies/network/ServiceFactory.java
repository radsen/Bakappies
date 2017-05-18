package com.udacity.bakappies.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by radsen on 5/10/17.
 */

public class ServiceFactory {

    // https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58d1537b_baking/baking.json
    // http://go.udacity.com/android-baking-app-json
    private static final String API_BASE_URL = "http://go.udacity.com";

    public static <T> T createService(final Class<T> clazz){

        final Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return restAdapter.create(clazz);
    }

}
