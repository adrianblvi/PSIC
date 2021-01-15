package com.example.spotifylogin;

import android.content.Intent;
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
    private HashMap<String, SpotifySong> hashsongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);

        Bundle bundle = getIntent().getExtras();
        String artist = (String) bundle.get("artist_name");
        try {
             hashsongs = mainActivity.readSongs(this);
            songs = mainActivity.artistSongs(hashsongs, artist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ListView listview = findViewById(R.id.lv_artist_songs);
        TextView tv = findViewById(R.id.tvArtist_song);
        tv.setText(artist +" songs");
        AdapterSongs adapterArtistSongs = new AdapterSongs(this, songs);
        listview.setAdapter(adapterArtistSongs);
        listview.setOnItemClickListener((parent, view, position, id) -> {
            ListItem clicked = songs.get(position);
            for (SpotifySong song : hashsongs.values()) {
                if (clicked.getTitle().trim().equals(song.getTitle().trim())) {
                    String id_to_send = song.getId();
                    Intent myIntent = new Intent(view.getContext(), recommendedSongs.class);
                    myIntent.putExtra("song_id",id_to_send);
                    startActivity(myIntent);
                }
            }
        });

    }
}