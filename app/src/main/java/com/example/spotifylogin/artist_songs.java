package com.example.spotifylogin;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class artist_songs extends AppCompatActivity {
    MainActivity mainActivity = new MainActivity();
    private ArrayList<ListItem> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);

        Bundle bundle = getIntent().getExtras();
        String artist = (String) bundle.get("artist_name");
        try {
            HashMap<String, SpotifySong> hashsongs = mainActivity.readSongs(this);
            songs = mainActivity.artistSongs(hashsongs, artist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ListView listview = findViewById(R.id.lv_artist_songs);
        TextView tv = findViewById(R.id.tvArtist_song);
        tv.setText(artist +" songs");
        AdapterSongs adapterArtistSongs = new AdapterSongs(this, songs);
        listview.setAdapter(adapterArtistSongs);

    }
}