package com.example.spotifylogin;
public class SongFeatures {
    private String id;
    private Double[] features;

    public SongFeatures(String id, Double[] features) {
        this.id = id;
        this.features = features;
    }

    public String getId() {
        return id;
    }

    public Double[] getFeatures() {
        return features;
    }
}
