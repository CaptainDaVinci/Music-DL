package com.example.application.musicdownloader.api;

import com.example.application.musicdownloader.api.github.service.GithubDataService;
import com.example.application.musicdownloader.api.server.service.ServerDataService;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClientInstance {
    private static final String GITHUB_URL = "https://api.github.com/repos/CaptainDaVinci/Music-DL/releases/";
    private static Retrofit serverRetrofit, githubRetrofit;
    private static ServerDataService serverDataService;
    private static GithubDataService githubDataService;


    private static Retrofit getServerRetrofitInstance() {
        String SERVER_URL = FirebaseRemoteConfig.getInstance().getString("SERVER_URL");
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

    private static Retrofit getGithubRetrofitInstance() {
        if (githubRetrofit == null) {
            githubRetrofit = new Retrofit.Builder()
                    .baseUrl(GITHUB_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return githubRetrofit;
    }

    public static ServerDataService getServerDataService() {
        if (serverDataService == null) {
            serverDataService = APIClientInstance.getServerRetrofitInstance().create(ServerDataService.class);
        }
        return serverDataService;
    }

    public static GithubDataService getGithubDataService() {
        if (githubDataService == null) {
            githubDataService = APIClientInstance.getGithubRetrofitInstance().create(GithubDataService.class);
        }
        return githubDataService;
    }
}
