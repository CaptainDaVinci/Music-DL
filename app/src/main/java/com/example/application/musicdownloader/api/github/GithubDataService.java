package com.example.application.musicdownloader.api.github;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GithubDataService {
    @GET("latest")
    Call<GithubData> getLatestRelease();
}
