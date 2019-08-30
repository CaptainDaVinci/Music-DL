package com.example.application.musicdownloader.api.youtube;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class YouTubeData {
    @SerializedName("items")
    private ArrayList<Item> items;

    public String getId() {
        return items.get(0).getId().getVideoId();
    }

    public String getTitle() {
        return items.get(0).getSnippet().getTitle();
    }

    public String getArtist() {
        return items.get(0).getSnippet().getArtist();
    }

    public String getThumbnailURL() {
        return items.get(0).getSnippet().getThumbnail().getaDefault().getUrl();
    }

    public String printDebug() {
        return getId() + " " + getArtist() + " " + getThumbnailURL();
    }

}

class Item {
    @SerializedName("id")
    private Id id;

    @SerializedName("snippet")
    private Snippet snippet;

    public Id getId() {
        return this.id;
    }

    public Snippet getSnippet() {
        return snippet;
    }
}

class Id {
    @SerializedName("videoId")
    private String videoId;

    public String getVideoId() {
        return this.videoId;
    }
}

class Snippet {
    @SerializedName("title")
    private String title;

    @SerializedName("channelTitle")
    private String artist;

    @SerializedName("thumbnails")
    private Thumbnail thumbnail;

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }

    public Thumbnail getThumbnail() {
        return this.thumbnail;
    }
}

class Thumbnail {
    @SerializedName("default")
    private Default aDefault;

    public Default getaDefault() {
        return this.aDefault;
    }
}

class Default {
    @SerializedName("url")
    private String url;

    public String getUrl() {
        return this.url;
    }
}
