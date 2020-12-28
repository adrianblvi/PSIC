package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Songs extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        Button btnHome = (Button) findViewById(R.id.homeButton);
        btnHome.setOnClickListener(v -> mainActivity.openActivity(this,MainActivity.class));

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
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<ListItem> songsList = new ArrayList<>();

        for (int i = 0; i < toArray.size(); i++) songsList.add(new ListItem(toArray.get(i).getTitle(),toArray.get(i).getArtist()));
        for (int i = 0; i < toArray.size(); i++) titles.add(toArray.get(i).getTitle());

        titles.sort(String::compareTo);
        songsList.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

        ListView listview = findViewById(R.id.listViewSongs);
        AdapterSongs adapterSongs = new AdapterSongs(this, songsList);
        listview.setAdapter(adapterSongs);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            String song_id = null;
            String title = titles.get(position);
            for (int i = 0; i < toArray.size(); i++) {
                if (title.equals(toArray.get(i).getTitle())) {
                    song_id = toArray.get(i).getId();
                }
            }
            Intent myIntent = new Intent(view.getContext(), recommendedSongs.class);
            myIntent.putExtra("song_id", song_id);
            startActivity(myIntent);
        }
        );
    }
}