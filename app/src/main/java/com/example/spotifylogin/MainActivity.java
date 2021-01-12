package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.spotifyapiexample://callback";
    private static final String CLIENT_ID = "863f528d3eea4a9ea598640d0e31895f";
    private ArrayList idPlaylist = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        Button btnClickMe = (Button) findViewById(R.id.newPlaylist);
        btnClickMe.setOnClickListener(v -> openActivity(this, NewPlaylist.class));

        Button btnSongs = (Button) findViewById(R.id.songsButton);
        btnSongs.setOnClickListener(v -> openActivity(this, Songs.class));

        Button btnArtists = (Button) findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v -> openActivity(this, Artists.class));

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

    public void contentBased(ArrayList<String> ids, HashMap<String, SongFeatures> features, HashMap<String, SpotifySong> songs) {
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
        System.out.println();
        System.out.println("*** Recommended Songs ***");
        for (int j = 0; j < 10; j++) {
            SpotifySong song = songs.get(results.get(j).getId());
            System.out.println(song.getTitle() + "  --  " + song.getArtists());
        }
    }
    public void artistSongs(HashMap<String, SpotifySong> songs) {
        ArrayList<String> artistSongs = new ArrayList<>();
        String artist = "Bad Bunny";
        System.out.println();
        System.out.println("Songs for : " + artist);
        for (SpotifySong song :
                songs.values()) {
            if (song.getArtists().contains(artist)) {
                artistSongs.add(song.getTitle() + " -- " + song.getArtists());
            }
        }
        for (String str :
                artistSongs) {
            System.out.println(str);
        }

    }
    public  ArrayList<ListItem> obtainTopRecommendation(String id, HashMap<String, SpotifySong> songs, HashMap<String, SongFeatures> features) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String authToken;

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case TOKEN:
                    authToken = response.getAccessToken();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL urlPlaylist = new URL("https://api.spotify.com/v1/me/playlists?limit=50");
                                HttpURLConnection urlConnection = (HttpURLConnection) urlPlaylist.openConnection();
                                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                                if (urlConnection.getResponseCode() != 200) {
                                    throw new RuntimeException("Failed: HTTP error code " + urlConnection.getResponseCode());
                                }
                                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                String input;
                                while ((input = in.readLine()) != null) {
                                    if (input.contains("href") && input.contains("tracks")) {
                                        //Log.e("MainActivity", input);
                                        String aux[] = input.split(":");
                                        for (int i = 0; i < aux.length; i++) {
                                            if (!(aux[i].contains("href")) && !aux[i].contains("https")) {
                                                idPlaylist.add(aux[i].replace("\",", ""));
                                            }
                                        }
                                    }
                                }
                                in.close();

                                for (int i = 0; i < idPlaylist.size(); i++) {
                                    URL urlTracks = new URL("https:" + idPlaylist.get(i).toString() + "?fields=items(track(id,name))");
                                    //URL urlTracks = new URL("https:" + idPlaylist.get(i).toString() + "?fields=items(track(id)))");
                                    urlConnection = (HttpURLConnection) urlTracks.openConnection();
                                    urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                                    if (urlConnection.getResponseCode() != 200) {
                                        throw new RuntimeException("Failed: HTTP error code " + urlConnection.getResponseCode());
                                    }
                                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                    StringBuilder b = new StringBuilder();
                                    while ((input = in.readLine()) != null) {
                                        b.append(input);
                                    }
                                    JSONObject j = new JSONObject(b.toString());
                                    Log.d("MainActivity", j.toString());
                                }
                                in.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case ERROR:
                    throw new RuntimeException("Failed to obtain the token");
                default:
                    // Handle other cases
            }
        }
    }

}