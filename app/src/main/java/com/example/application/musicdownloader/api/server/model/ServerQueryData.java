package com.example.application.musicdownloader.api.server.model;

import com.google.gson.annotations.SerializedName;

public class ServerQueryData {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
