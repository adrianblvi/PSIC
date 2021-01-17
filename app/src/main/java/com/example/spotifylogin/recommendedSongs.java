package com.example.spotifylogin;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class recommendedSongs extends AppCompatActivity {

    SpotifyLogin spotifyLogin = new SpotifyLogin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_songs);

        Bundle bundle = getIntent().getExtras();
        String song_id = (String) bundle.get("song_id");
        String song_artist_name = (String) bundle.get("title");

        HashMap<String, SongFeatures> hashVectors;
        HashMap<String, SpotifySong> hashSongArtist;
        ArrayList<ListItem> songsList = new ArrayList<>();
        try {
            hashVectors = spotifyLogin.readFeatures(this);
            hashSongArtist = spotifyLogin.readSongs(this);
            songsList = spotifyLogin.obtainTopRecommendation(song_id, hashSongArtist, hashVectors);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<ListItem> hashSet = new LinkedHashSet<>(songsList);
        hashSet.addAll(songsList);
        songsList.clear();
        songsList.addAll(hashSet);
        songsList.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

        String [] artists = song_artist_name.trim().split(",");
        TextView textView = findViewById(R.id.textView_reco_songs);

        if(artists.length>1) textView.setText(String.format("Based On \"%s, %s, ...\"", artists[0].trim(),artists[1].trim()));
        else textView.setText(String.format("Based On \"%s\"", song_artist_name.trim()));

        ListView listview = findViewById(R.id.list_view_reco_songs);
        AdapterSongs adapterRecommendedSongs = new AdapterSongs(this, songsList);
        listview.setAdapter(adapterRecommendedSongs);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            TextView title = view.findViewById(R.id.textView_title);
            TextView artist = view.findViewById(R.id.textView_artist);
            Snackbar snackbar = Snackbar.make(view,title.getText().toString()+" feat "+artist.getText().toString()+" added to your playlist" , BaseTransientBottomBar.LENGTH_SHORT);
            snackbar.show();
            String filename = "play_lists.txt";
            spotifyLogin.writeFile(this,title,artist,filename);
        });
    }
}