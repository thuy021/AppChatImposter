package com.example.appchat.Retrofit2;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private  static Retrofit retrofit = null;

    public static  Retrofit getClient(String url){
        OkHttpClient builder = new OkHttpClient.Builder()
                                        .readTimeout(5000, TimeUnit.MILLISECONDS)
                                        .writeTimeout(5000, TimeUnit.MILLISECONDS)
                                        .connectTimeout(10000, TimeUnit.MILLISECONDS)
                                        .retryOnConnectionFailure(true)
                                        .build();

        Gson gson  = new GsonBuilder().setLenient().create();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(builder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }
}
