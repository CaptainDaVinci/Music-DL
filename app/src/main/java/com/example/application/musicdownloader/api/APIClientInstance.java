package com.example.application.musicdownloader.api;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientInstance {
    public static final String YOUTUBE_API_KEY = FirebaseRemoteConfig.getInstance().getString("YOUTUBE_API_KEY");
    private static final String YOUTUBE_URL = "https://www.googleapis.com/youtube/v3/";
    private static final String SERVER_URL = FirebaseRemoteConfig.getInstance().getString("SERVER_URL");
    private static Retrofit youTubeRetrofit, serverRetrofit;

    public static Retrofit getYouTubeRetrofitInstance() {
        if (youTubeRetrofit == null) {
            youTubeRetrofit = new Retrofit.Builder()
                    .baseUrl(YOUTUBE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return youTubeRetrofit;
    }

    public static Retrofit getServerRetrofitInstance() {
        if (serverRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS)
                    .readTimeout(600, TimeUnit.SECONDS)
                    .build();

            serverRetrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return serverRetrofit;
    }
}
