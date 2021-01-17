package com.example.spotifylogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class artist_songs extends AppCompatActivity {
    SpotifyLogin spotifyLogin = new SpotifyLogin();
    HashMap<String, SpotifySong> hashsongs;
    private ArrayList<ListItem> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_songs);

        Bundle bundle = getIntent().getExtras();
        String artist_name = (String) bundle.get("artist_name");
        ArrayList <String> id_songs_send = bundle.getStringArrayList("id_songs_send");
        String playlist_name = bundle.getString("namePlaylist");
        ArrayList <String> id_songs = bundle.getStringArrayList("id_songs");
        ArrayList <String> spotify_playlists = bundle.getStringArrayList("spotify_playlists");
        String [] username = bundle.getStringArray("username");

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
            intent.putExtra("id_songs_send",id_songs_send);
            intent.putExtra("namePlaylist",playlist_name);
            this.startActivity(intent);
        });
        Button btnArtists = findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v -> {
            Intent intent = new Intent(this, Artists.class);
            intent.putExtra("id_songs", id_songs);
            intent.putExtra("spotify_playlists", spotify_playlists);
            intent.putExtra("username", username);
            intent.putExtra("id_songs_send",id_songs_send);
            intent.putExtra("namePlaylist",playlist_name);
            this.startActivity(intent);
        });
        try {
            hashsongs = spotifyLogin.readSongs(this);
            songs = spotifyLogin.artistSongs(hashsongs, artist_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ListView listview = findViewById(R.id.lv_artist_songs);
        TextView tv = findViewById(R.id.tvArtist_song);
        String [] artists = artist_name.split(",");
        if(artists.length>1) tv.setText(String.format("More of \"%s, %s, ....\"", artists[0].trim(),artists[1].trim()));
        else tv.setText(String.format("More of \"%s\"", artist_name.trim()));

        songs.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

        AdapterSongs adapterArtistSongs = new AdapterSongs(this, songs);
        listview.setAdapter(adapterArtistSongs);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            ListItem clicked = songs.get(position);
            for (SpotifySong song : hashsongs.values()) {
                if (clicked.getTitle().trim().equals(song.getTitle().trim())) {
                    String id_to_send = song.getId();
                    Intent myIntent = new Intent(view.getContext(), recommendedSongs.class);
                    myIntent.putExtra("title",songs.get(position).getArtist());
                    myIntent.putExtra("song_id",id_to_send);
                    startActivity(myIntent);
                }
            }
        });

        /*listview.setOnItemClickListener((parent, view, position, id)->{
            TextView title = view.findViewById(R.id.textView_title);
            TextView artist = view.findViewById(R.id.textView_artist);
            Snackbar snackbar = Snackbar.make(view,title.getText().toString()+" feat "+artist.getText().toString()+" added to your playlist" , BaseTransientBottomBar.LENGTH_SHORT);
            snackbar.show();
            String filename = "play_lists.txt";
            spotifyLogin.writeFile(this,title,artist,filename);
        });*/
    }
}