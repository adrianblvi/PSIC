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

public class recommendedSongs extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_songs);

        Bundle bundle = getIntent().getExtras();
        String song_id = (String) bundle.get("song_id");

        HashMap<String, SongVector> hashVectors;
        HashMap<String, SongArtist> hashSongArtist;
        ArrayList<ListItem> songsList = new ArrayList<>();

        try {
            hashVectors = mainActivity.readVectorsFile(this);
            hashSongArtist = mainActivity.readSongsArtistFile(this);
            songsList = mainActivity.obtainTopnRecommendations(song_id, hashSongArtist, hashVectors, 20);
        } catch (IOException e) {
            e.printStackTrace();
        }

        songsList.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

        ListView listview = findViewById(R.id.list_view_reco_songs);
        AdapterSongs adapterRecommendedSongs = new AdapterSongs(this, songsList);
        listview.setAdapter(adapterRecommendedSongs);

        listview.setOnItemClickListener((parent, view, position, id) -> {

            TextView title = (TextView) view.findViewById(R.id.textView_title);
            TextView artist = (TextView) view.findViewById(R.id.textView_artist);

            //pw.println(title.getText().toString()+','+artist.getText().toString());
        });
    }
}