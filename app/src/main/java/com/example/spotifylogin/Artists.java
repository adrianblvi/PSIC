package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Artists extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        Button btnHome = (Button) findViewById(R.id.homeButton);
        btnHome.setOnClickListener((v -> mainActivity.openActivity(this,MainActivity.class)));

        Button btnClickMe = (Button) findViewById(R.id.newPlaylist);
        btnClickMe.setOnClickListener(v -> mainActivity.openActivity(this,NewPlaylist.class));

        Button btnSongs = (Button) findViewById(R.id.songsButton);
        btnSongs.setOnClickListener(v -> mainActivity.openActivity(this,Songs.class));

        Button btnArtists = (Button) findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v -> mainActivity.openActivity(this,Artists.class));

        HashMap<String, SongArtist> hashSongArtist = new HashMap<>();

        try {
            hashSongArtist = mainActivity.readSongsArtistFile(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SongArtist> toArray = new ArrayList<>(hashSongArtist.values());
        ArrayList<String> artists = new ArrayList<>();
        ArrayList<ListItem> songsList = new ArrayList<>();

        for (int i = 0; i < toArray.size(); i++) {
            artists.add(toArray.get(i).getArtist());
        }

        Set<String> hashSet = new HashSet<String>(artists);
        artists.clear();
        artists.addAll(hashSet);
        artists.sort(String::compareTo);

        for (int i = 0; i < artists.size(); i++) songsList.add(new ListItem(artists.get(i),artists.get(i)));

        AdapterArtists adapterArtists = new AdapterArtists(this, songsList);

        ListView listview = findViewById(R.id.listViewArtists);
        listview.setAdapter(adapterArtists);

    }
}
