package com.example.application.musicdownloader.api.server;

import com.google.gson.annotations.SerializedName;

public class ServerData {
    @SerializedName("download_link")
    private String downloadLink;

    public String getDownloadLink() {
        return this.downloadLink;
    }
}
