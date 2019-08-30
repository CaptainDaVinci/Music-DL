package com.example.application.musicdownloader.api.server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerDataService {
    @GET("download")
    Call<ServerData> getDownloadLink(
            @Query("id") String id,
            @Query("encoding") String format,
            @Query("quality") String quality
    );
}
