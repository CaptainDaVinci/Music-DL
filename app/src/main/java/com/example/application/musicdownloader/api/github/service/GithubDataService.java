package com.example.application.musicdownloader.api.github.service;

import com.example.application.musicdownloader.api.github.model.GithubData;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GithubDataService {
    @GET("latest")
    Call<GithubData> getLatestRelease();
}
