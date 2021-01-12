package com.example.spotifylogin;

public class SpotifySong {
    private String id;
    private String title;
    private String artists;

    public SpotifySong(String id, String title, String artists) {
        this.id = id;
        this.title = title;
        this.artists = artists;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtists() {
        return artists;
    }
}
