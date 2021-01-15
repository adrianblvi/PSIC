package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

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
        btnHome.setOnClickListener((v -> mainActivity.openActivity(this, MainActivity.class)));

        Button btnClickMe = (Button) findViewById(R.id.newPlaylist);
        btnClickMe.setOnClickListener(v -> mainActivity.openActivity(this, NewPlaylist.class));

        Button btnSongs = (Button) findViewById(R.id.songsButton);
        btnSongs.setOnClickListener(v -> mainActivity.openActivity(this, Songs.class));

        Button btnArtists = (Button) findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v -> mainActivity.openActivity(this, Artists.class));

        HashMap<String, SpotifySong> hashSongArtist = new HashMap<>();

        try {
            hashSongArtist = mainActivity.readSongs(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SpotifySong> toArray = new ArrayList<>(hashSongArtist.values());
        ArrayList<String> artists = new ArrayList<>();
        ArrayList<ListItem> songsList = new ArrayList<>();

        for (SpotifySong song :
                hashSongArtist.values()) {
            String[] data = song.getArtists().trim().split(",");
            for (int i = 0; i < data.length; i++) {
                artists.add(data[0].replace('"', ' ').trim());
            }
        }

        Set<String> hashSet = new HashSet<String>(artists);
        artists.clear();
        artists.addAll(hashSet);
        for (int i = 0; i < artists.size(); i++) {
            ListItem item = new ListItem("Da igual", artists.get(i));
            songsList.add(item);
        }

        ListView lv = (ListView) findViewById(R.id.listViewArtists);
        SearchView sv = (SearchView) findViewById(R.id.search);


//        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, artists);
        AdapterArtists artAdapter = new AdapterArtists(this, songsList);
        lv.setAdapter(artAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            String artist_name = songsList.get(position).getArtist();
            Intent myIntent = new Intent(view.getContext(), artist_songs.class);
            myIntent.putExtra("artist_name", artist_name);
            startActivity(myIntent);
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
                artAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}
