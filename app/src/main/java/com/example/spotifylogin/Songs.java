package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Songs extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SpotifyLogin spotifyLogin = new SpotifyLogin();
    private ArrayList<ListItem> songList;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private recyclerAdapter adapter;
    private HashMap<String, SpotifySong> hashSongArtist;
    private HashMap<String, SongFeatures> features;
    ArrayList<SpotifySong> playlist_songs = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String> id_songs_send = bundle.getStringArrayList("id_songs_send");
        String playlist_name = bundle.getString("namePlaylist");
        ArrayList<String> id_songs = bundle.getStringArrayList("id_songs");
        ArrayList<String> spotify_playlists = bundle.getStringArrayList("spotify_playlists");
        String[] username = bundle.getStringArray("username");

        Button btnClickMe = findViewById(R.id.newPlaylist);
        btnClickMe.setOnClickListener(v -> {
            Intent intent = new Intent(this, SpotifyLogin.class);
            intent.putExtra("id_songs", id_songs);
            intent.putExtra("spotify_playlists", spotify_playlists);
            intent.putExtra("username", username);
            this.startActivity(intent);
        });
        Button btnSongs = findViewById(R.id.songsButton);
        btnSongs.setOnClickListener(v -> {
            Intent intent = new Intent(this, Songs.class);
            intent.putExtra("id_songs", id_songs);
            intent.putExtra("spotify_playlists", spotify_playlists);
            intent.putExtra("username", username);
            intent.putExtra("id_songs_send", id_songs_send);
            intent.putExtra("namePlaylist", playlist_name);
            this.startActivity(intent);
        });
        Button btnArtists = findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v -> {
            Intent intent = new Intent(this, Artists.class);
            intent.putExtra("id_songs", id_songs);
            intent.putExtra("spotify_playlists", spotify_playlists);
            intent.putExtra("username", username);
            intent.putExtra("id_songs_send", id_songs_send);
            intent.putExtra("namePlaylist", playlist_name);
            this.startActivity(intent);
        });

        hashSongArtist = new HashMap<>();
        features = new HashMap<>();
        try {
            hashSongArtist = spotifyLogin.readSongs(this);
//            features = spotifyLogin.readFeatures(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < id_songs_send.size(); i++) {
            SpotifySong spotifySong = hashSongArtist.get(id_songs_send.get(i));
            if (spotifySong != null) playlist_songs.add(spotifySong);
        }
        recyclerView = findViewById(R.id.search);
        searchView = findViewById(R.id.searchView);
        songList = new ArrayList<>();

        TextView textView = findViewById(R.id.textView6);
        textView.setText(playlist_name.trim());

        if(playlist_name.startsWith("Based on")){
            ArrayList<ListItem> aux = new ArrayList<>();
            aux.add(new ListItem("Cantando"," Kase.O"));
            songList.addAll(aux);
        }else {
            for (int i = 0; i < playlist_songs.size(); i++) {
                songList.add(new ListItem(playlist_songs.get(i).getTitle().trim(), playlist_songs.get(i).getArtists()));
            }
        }

        songList.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

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
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            ListItem clicked = songList.get(position);
            for (SpotifySong song : playlist_songs) {
                if (clicked.getTitle().trim().equals(song.getTitle().trim())) {
                    String id = song.getId();
                    Intent myIntent = new Intent(recyclerView.getContext(), recommendedSongs.class);
                    myIntent.putExtra("song_id", id);
                    myIntent.putExtra("title", songList.get(position).getTitle());
                    startActivity(myIntent);
                }
            }
        });
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