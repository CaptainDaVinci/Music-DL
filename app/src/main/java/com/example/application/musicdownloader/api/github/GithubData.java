package com.example.application.musicdownloader.api.github;

import com.google.gson.annotations.SerializedName;

public class GithubData {
    @SerializedName("tag_name")
    private String tag_name;

    @SerializedName("html_url")
    private String html_url;

    public String getTag_name() {
        return tag_name;
    }

    public String getHtml_url() {
        return html_url;
    }
}
