package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Songs extends AppCompatActivity implements SearchView.OnQueryTextListener {

    MainActivity mainActivity = new MainActivity();
    private ArrayList<ListItem> songList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private recyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        Button btnHome = (Button) findViewById(R.id.homeButton);
        btnHome.setOnClickListener(v -> mainActivity.openActivity(this, MainActivity.class));

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
        recyclerView = findViewById(R.id.search);
        searchView = findViewById(R.id.searchView);
        songList = new ArrayList<>();
        for (SpotifySong song : hashSongArtist.values()) {
            songList.add(new ListItem(song.getTitle(), song.getArtists()));
        }
        setAdapter();
        initListener();

    }

    private void initListener() {
        searchView.setOnQueryTextListener(this);
    }

    private void setAdapter() {
        adapter = new recyclerAdapter(songList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }
}