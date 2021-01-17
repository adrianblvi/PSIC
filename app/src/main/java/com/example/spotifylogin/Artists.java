package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

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

    SpotifyLogin spotifyLogin = new SpotifyLogin();
    ArrayList<SpotifySong> playlist_songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

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

        HashMap<String, SpotifySong> hashSongArtist = new HashMap<>();

        try {
            hashSongArtist = spotifyLogin.readSongs(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

//      ArrayList<SpotifySong> toArray = new ArrayList<>(hashSongArtist.values());
//      ArrayList<ListItem> songsList = new ArrayList<>();

        for (int i = 0; i < id_songs_send.size(); i++) {
            SpotifySong spotifySong = hashSongArtist.get(id_songs_send.get(i));
            if (spotifySong != null) playlist_songs.add(spotifySong);
        }
        /*for (SpotifySong song :
                hashSongArtist.values()) {
            String[] data = song.getArtists().trim().split(",");
            for (int i = 0; i < data.length; i++) {
                artists.add(data[0].replace('"', ' ').trim());
            }
        } e.printStackTrace();
        }*/
        ArrayList<String> songList = new ArrayList<>();
        ArrayList<String> aux_list = new ArrayList<>();
        ArrayList<ListItem> artists = new ArrayList<>();

        for (int i = 0; i < playlist_songs.size(); i++) {
            String artist = playlist_songs.get(i).getArtists();
            String[] data = artist.trim().split(",");
            for (int j = 0; j < data.length; j++) {
                songList.add(data[j].replace('"', ' ').trim());
            }
        }
        Set<String> hashSet = new HashSet<>(songList);
        songList.clear();
        songList.addAll(hashSet);
        songList.sort(String::compareTo);
//
//        for (int i = 0; i < artists.size(); i++)
//            songsList.add(new ListItem(artists.get(i), artists.get(i)));
//
//        AdapterArtists adapterArtists = new AdapterArtists(this, songsList);
//
//        ListView listview = findViewById(R.id.listViewArtists);
//        listview.setAdapter(adapterArtists);

        TextView textView = findViewById(R.id.textView6);
        textView.setText(playlist_name.trim());

        ListView lv = (ListView) findViewById(R.id.listViewArtists);
        SearchView sv = findViewById(R.id.search);

        for (int i = 0; i < songList.size(); i++) {
            ListItem item = new ListItem("Es esto", songList.get(i));
            artists.add(item);
        }

        //ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        AdapterArtists artAdapter = new AdapterArtists(this, artists);
        lv.setAdapter(artAdapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            String artist_name = artists.get(position).getArtist();
            Intent intent = new Intent(view.getContext(), artist_songs.class);
            intent.putExtra("artist_name", artist_name);
            intent.putExtra("id_songs", id_songs);
            intent.putExtra("spotify_playlists", spotify_playlists);
            intent.putExtra("username", username);
            intent.putExtra("id_songs_send", id_songs_send);
            intent.putExtra("namePlaylist", playlist_name);
            startActivity(intent);
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                artAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}