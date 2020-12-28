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
        btnClickMe.setOnClickListener(v -> openActivity(this,NewPlaylist.class));

        Button btnSongs = (Button) findViewById(R.id.songsButton);
        btnSongs.setOnClickListener(v ->  openActivity(this,Songs.class));

        Button btnArtists = (Button) findViewById(R.id.artistButton);
        btnArtists.setOnClickListener(v ->  openActivity(this,Artists.class));

//        try {
//            this.readVectorsFile();
//            this.readSongsArtistFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public ArrayList<ListItem> obtainTopnRecommendations(String song_id, HashMap<String, SongArtist> hashSongArtist,
                                                         HashMap<String, SongVector> hashVectors, int n) {

        HashMap<String, SongCosine> hashCosineResults = new HashMap<>();
        ArrayList<ListItem> songsList = new ArrayList<>();
        SongArtist search = hashSongArtist.get(song_id);

        if (search == null) {
            System.out.println("Song with id " + song_id + " doesnt exist");
        } else {
            Double[] A = hashVectors.get(song_id).getVector();
            for (SongVector string : hashVectors.values()) {
                if (!(string.getId().equals(song_id))) {
                    Double[] B = string.getVector();
                    double cosineValue = calculateCosineSimilarity(A, B);
                    hashCosineResults.put(string.getId(), new SongCosine(string.getId(), cosineValue));
                }
            }
            ArrayList<SongCosine> arrayCosine = new ArrayList<>(hashCosineResults.values());
            Collections.sort(arrayCosine);
            ArrayList<String> recommendedTitles = new ArrayList<>();
//            System.out.println("Top " + n + " recommendations for: " + search.getTitle() + " ---- " + search.getArtist() + "\n");
            for (int i = 0; i < n; i++) {
                String idToSearch = arrayCosine.get(i).getId();
                SongArtist toRecommend = hashSongArtist.get(idToSearch);
                songsList.add(new ListItem(toRecommend.getTitle(),toRecommend.getArtist()));
            }
            return  songsList;
        }


        return null;
    }
    public HashMap<String, SongVector> readVectorsFile(Context context) throws IOException {
        HashMap<String, SongVector> hashVectors = new HashMap<>();
        try {
            InputStream fileReader = context.getResources().openRawResource(R.raw.song_vectors);
            BufferedReader buffereReader = new BufferedReader(new InputStreamReader(fileReader));
            String line = "";
            while ((line = buffereReader.readLine()) != null) {

                line = buffereReader.readLine();
                if (line != null) {
                    String[] data = line.split(",");
                    String song_id = data[0];
                    Double vector[] = new Double[32];
                    for (int i = 0; i < vector.length; i++) {
                        vector[i] = Double.valueOf(data[i + 1]);
                    }
                    hashVectors.put(song_id, new SongVector(song_id, vector));
                }
            }

            buffereReader.close();
            fileReader.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return hashVectors;
    }

    public HashMap<String, SongArtist> readSongsArtistFile(Context context) throws IOException {
        HashMap<String, SongArtist> hashSongArtist = new HashMap<>();
        try {
            InputStream fileReader = context.getResources().openRawResource(R.raw.song_mapper);
            BufferedReader buffereReader = new BufferedReader(new InputStreamReader(fileReader));
            String line = "";
            while ((line = buffereReader.readLine()) != null) {

                line = buffereReader.readLine();
                // System.out.println(line);
                if (line == null) {
                    break;
                } else {
                    String[] data = line.split(",");
                    hashSongArtist.put(data[0], new SongArtist(data[0], data[1], data[2]));
                }
            }

            buffereReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashSongArtist;
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