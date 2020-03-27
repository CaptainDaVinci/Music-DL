package com.example.application.musicdownloader.api.server.model;

import com.google.gson.annotations.SerializedName;

public class ServerDownloadData {
    @SerializedName("download_link")
    private String downloadLink;

    public String getDownloadLink() {
        return this.downloadLink;
    }
}
