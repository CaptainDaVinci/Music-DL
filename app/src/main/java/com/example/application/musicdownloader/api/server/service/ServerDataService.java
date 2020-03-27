package com.example.application.musicdownloader.api.server.service;

import com.example.application.musicdownloader.api.server.model.ServerDownloadData;
import com.example.application.musicdownloader.api.server.model.ServerQueryData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerDataService {
    @GET("download")
    Call<ServerDownloadData> getDownloadLink(
            @Query("id") String id,
            @Query("encoding") String format,
            @Query("quality") String quality
    );

    @GET("id")
    Call<ServerQueryData> getYouTubeId(
            @Query("q") String query
    );
}
