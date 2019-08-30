package com.example.application.musicdownloader.query;

public class Query {
    private String search;
    private Quality quality;
    private Encoding encoding;

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Quality getQuality() {
        return this.quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Encoding getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return this.search + " " + this.encoding.toString() + " " + this.quality.toString();
    }
}
