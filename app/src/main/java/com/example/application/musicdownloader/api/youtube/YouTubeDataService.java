package com.example.application.musicdownloader.api.youtube;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeDataService {
    @GET("search")
    Call<YouTubeData> getYouTubeData(
            @Query("part") String part,
            @Query("q") String query,
            @Query("type") String type,
            @Query("maxResults") String maxResults,
            @Query("key") String key
    );
}
