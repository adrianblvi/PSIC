package com.example.spotifylogin;

public class ListItem {
        private String title;
        private final String artist;

        public ListItem(String title, String artist) {
            this.title = title;
            this.artist = artist;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getArtist() {
            return artist;
        }
    }

