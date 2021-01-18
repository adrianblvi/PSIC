package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class SpotifyLogin extends AppCompatActivity {
    private ArrayList<String> id_songs;
    private ArrayList<String> spotify_playlists;
    private String[] username;
    private static final String REDIRECT_URI = "com.spotifyapiexample://callback";
    private static final String CLIENT_ID = "863f528d3eea4a9ea598640d0e31895f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bundle = getIntent().getExtras();

        id_songs = bundle.getStringArrayList("id_songs");
        spotify_playlists = bundle.getStringArrayList("spotify_playlists");
        username = bundle.getStringArray("username");

        TextView textView = findViewById(R.id.userName);
        if (username[0] != null) textView.setText(username[0].replace("\"", ""));
        ArrayList<String> correctNames = new ArrayList<>();
        ArrayList<String> backup = new ArrayList<>();
        for (int i = 0; i < spotify_playlists.size(); i++) {
            String name = "Based on " + spotify_playlists.get(i).replace("\"", "").trim();
            correctNames.add(spotify_playlists.get(i).replace("\"", "").trim());
            backup.add(name);
        }
        correctNames.addAll(backup);
        correctNames.add("Recommended songs playlist");
        Button btnLogOut = findViewById(R.id.logOut);
        btnLogOut.setOnClickListener(v -> {
            openActivity(this, MainActivity.class);
            logout();
        });

        ListView listview = findViewById(R.id.Spotify_playlists);
        AdapterPlaylists adapterPlaylists = new AdapterPlaylists(this, correctNames);
        listview.setAdapter(adapterPlaylists);

        listview.setOnItemClickListener((parent, view, position, id) -> {
                    ArrayList<String> id_songs_send = new ArrayList<>();
                    if (correctNames.get(position).equals("Recommended songs playlist")) {
                        Intent myIntent = new Intent(this, NewPlaylist.class);
                        myIntent.putExtra("username", username);
                        myIntent.putExtra("spotify_playlists", spotify_playlists);
                        myIntent.putExtra("id_songs", id_songs);
                        startActivity(myIntent);
                    }else{
                        for (int i = 0; i < id_songs.size(); i++) {
                            String[] contentSplit = id_songs.get(i).split(",");
                            if (Integer.parseInt(contentSplit[0]) == position) {
                                String[] id_split = contentSplit[1].replace("}", "").split(":");
                                id_songs_send.add(id_split[2].replace("\"", ""));
                            }
                        }
                        Intent myIntent = new Intent(this, Songs.class);
                        myIntent.putExtra("id_songs_send", id_songs_send);
                        myIntent.putExtra("namePlaylist", correctNames.get(position));
                        myIntent.putExtra("id_songs", id_songs);
                        myIntent.putExtra("spotify_playlists", spotify_playlists);
                        myIntent.putExtra("username", username);
                        startActivity(myIntent);
                    }
                }
        );
    }

    public void logout(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        builder.setShowDialog(true);

    }
    public ArrayList<ListItem> readFile(Context context) {
        FileInputStream fis = null;
        ArrayList<ListItem> songsList = new ArrayList<>();
        try {
            fis = context.openFileInput("play_lists.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            System.out.println();
            while (line != null) {
                String[] splitLine = line.split(",");
                songsList.add(new ListItem(splitLine[0], splitLine[1]));
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }

        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            String contents = stringBuilder.toString();
        }

        return songsList;
    }

    public void writeFile(Context context, TextView title, TextView artist, String filename) {
        ArrayList<ListItem> listPlayList;
        listPlayList = readFile(context);
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND)) {
            fos.write((title.getText().toString() + "," + artist.getText().toString() + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void overwriteFile(Context context, String filename) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(("").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openActivity(Context context, Class parameterClass) {
        Intent intent = new Intent(context, parameterClass);
        context.startActivity(intent);
    }

    public static double calculateCosineSimilarity(Double[] A, Double[] B) {
        if (A == null || B == null || A.length == 0 || B.length == 0 || A.length != B.length) {
            return 2;
        }

        double sumProduct = 0;
        double sumASq = 0;
        double sumBSq = 0;
        for (int i = 0; i < A.length; i++) {
            sumProduct += A[i] * B[i];
            sumASq += A[i] * A[i];
            sumBSq += B[i] * B[i];
        }
        if (sumASq == 0 && sumBSq == 0) {
            return 2.0;
        }
        return sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
    }

    public ArrayList<ListItem> obtainTopRecommendation(String id, HashMap<String, SpotifySong> songs, HashMap<String, SongFeatures> features) {
        HashMap<String, SongCosine> results = new HashMap<>();
        ArrayList<ListItem> songsList = new ArrayList<>();
        SongFeatures toObtain = features.get(id);
        Double[] vectorA = toObtain.getFeatures();
        for (SongFeatures song :
                features.values()) {
            if (!(song.getId().equals(id))) {
                Double[] vectorB = song.getFeatures();
                Double cosine = calculateCosineSimilarity(vectorA, vectorB);
                results.put(song.getId(), new SongCosine(song.getId(), cosine));
            } else {
                results.put(song.getId(), new SongCosine(song.getId(), 0.00));
            }

        }
        ArrayList<SongCosine> toSort = new ArrayList<>(results.values());
        Collections.sort(toSort);
        for (int i = 0; i < 15; i++) {
            SpotifySong toRecommend = songs.get(toSort.get(i).getId());
            songsList.add(new ListItem(toRecommend.getTitle(), toRecommend.getArtists()));
        }
        return songsList;
    }


    public HashMap<String, SpotifySong> readSongs(Context context) throws IOException {

        HashMap<String, SpotifySong> songs = new HashMap<>();
        try {
            InputStream fileReader = context.getResources().openRawResource(R.raw.songs_info);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fileReader));
            String line = "";
            while ((line = bufferReader.readLine()) != null) {
                String[] data = line.trim().split(";");
                SpotifySong song = new SpotifySong(data[0], data[1], data[2].trim().replace('[', ' ').replace(']', ' ').replace("'", " "));
                songs.put(song.getId(), song);
            }
            bufferReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return songs;
    }

    public ArrayList<ListItem> artistSongs(HashMap<String, SpotifySong> songs, String artist) throws IOException {
        ArrayList<ListItem> artistSongs = new ArrayList<>();
        for (SpotifySong song :
                songs.values()) {
            if (song.getArtists().contains(artist)) {
                ListItem song_to_add = new ListItem(song.getTitle(), song.getArtists());
                artistSongs.add(song_to_add);

            }
        }
        return artistSongs;

    }

    public HashMap<String, SongFeatures> readFeatures(Context context) throws IOException {
        HashMap<String, SongFeatures> features = new HashMap<>();
        try {
            InputStream fileReader = context.getResources().openRawResource(R.raw.song_features);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(fileReader));
            String line = "";
            int j = 0;
            while ((line = bufferReader.readLine()) != null) {
                if (j != 0) {
                    String[] data = line.split(",");
                    String song_id = data[0];
                    Double vector[] = new Double[23];
                    for (int i = 0; i < vector.length; i++) {
                        vector[i] = Double.valueOf(data[i + 1]);
                    }
                    features.put(song_id, new SongFeatures(song_id, vector));
                } else {
                    j++;
                }
            }
            bufferReader.close();
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return features;
    }

    public ArrayList<ListItem> contentBased(ArrayList<String> ids, HashMap<String, SongFeatures> features, HashMap<String, SpotifySong> songs) {
        ArrayList<SongFeatures> playlist = new ArrayList<>();
        ArrayList<SongCosine> results = new ArrayList<>();
        HashMap<String, SongFeatures> copied = (HashMap<String, SongFeatures>) features.clone();
        for (String id : ids
        ) {
            SongFeatures featToAdd = features.get(id);
            if (featToAdd != null) {
                playlist.add(featToAdd);
            }
        }
        Double[] vector = new Double[23];
        int i = 0;
        for (SongFeatures string :
                playlist) {
            if (i == 0) {
                vector = string.getFeatures();
                i++;
            } else {
                Double[] vectorB = string.getFeatures();
                for (int j = 0; j < vectorB.length; j++) {
                    vector[j] += vectorB[j];
                }
            }
        }
        for (int j = 0; j < vector.length; j++) {
            vector[j] = vector[j] / vector.length;
        }
        Double[] userVector = vector;
        for (SongFeatures string :
                playlist) {
            String id = string.getId();
            copied.remove(id);
        }
        for (SongFeatures feat :
                copied.values()) {
            Double[] vectorB = feat.getFeatures();
            Double cosine = calculateCosineSimilarity(userVector, vectorB);
            SongCosine songToadd = new SongCosine(feat.getId(), cosine);
            results.add(songToadd);
        }
        Collections.sort(results);
        ArrayList<ListItem> to_ret = new ArrayList<>();
        for(int k=0;i<playlist.size();k++){
            SpotifySong aux = songs.get(results.get(k).getId());
            ListItem item = new ListItem(aux.getTitle(),aux.getArtists());
            to_ret.add(item);
        }
        return to_ret;
    }

}