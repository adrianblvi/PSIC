package com.example.spotifylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.spotifyapiexample://callback";
    private static final String CLIENT_ID = "863f528d3eea4a9ea598640d0e31895f";
    AuthenticationRequest request;
    private ArrayList idPlaylist = new ArrayList<String>();
    ArrayList <String> namePlaylist = new ArrayList<>();
    ArrayList <String> id_songs = new ArrayList<>();
    String[] userName = new String[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_login);

        Button btnLogin = findViewById(R.id.button);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        request = builder.build();
        builder.setScopes(new String[]{"streaming"});


        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        btnLogin.setOnClickListener(v -> {
            Intent myIntent = new Intent(this, SpotifyLogin.class);
            myIntent.putExtra("id_songs", id_songs);
            myIntent.putExtra("spotify_playlists", namePlaylist);
            myIntent.putExtra("username",userName);
            startActivity(myIntent);
        });

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
                    new Thread(() -> {
                        try  {
                            URL urlPlaylist = new URL("https://api.spotify.com/v1/me");
                            HttpURLConnection urlConnection = (HttpURLConnection) urlPlaylist.openConnection();
                            urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                            if(urlConnection.getResponseCode() != 200){
                                throw new RuntimeException("Failed: HTTP error code "+urlConnection.getResponseCode());
                            }
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            String input;
                            while((input = in.readLine()) != null){
                                if(input.contains("display_name")){
                                    String[] aux = input.split(":");
                                    userName[0] = aux[1].replace(",","");
                                }
                            }
                            urlPlaylist = new URL("https://api.spotify.com/v1/me/playlists?limit=50");
                            urlConnection = (HttpURLConnection) urlPlaylist.openConnection();
                            urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                            if(urlConnection.getResponseCode() != 200){
                                throw new RuntimeException("Failed: HTTP error code "+urlConnection.getResponseCode());
                            }
                            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            while((input = in.readLine()) != null) {
                                if(input.contains("name")){
                                    String[] aux = input.split(":");
                                    if(aux[0].replace("\"","").trim().equals("name")){
                                        namePlaylist.add(aux[1].replace(",",""));
                                    }
                                }
                                if(input.contains("href") && input.contains("tracks")) {
                                    // Log.e("MainActivity", input);
                                    String[] aux = input.split(":");
                                    for (String s : aux) {
                                        if (!(s.contains("href")) && !s.contains("https")) {
                                            idPlaylist.add(s.replace("\",", ""));
                                        }
                                    }
                                }
                            }
                            in.close();
                            for (int i= 0 ; i< namePlaylist.size() ;i++){
                                Log.d("MainActivity", namePlaylist.get(i));
                            }
                            for (int i=0; i<idPlaylist.size(); i++){
                                URL urlTracks = new URL("https:"+idPlaylist.get(i).toString()+"?fields=items(track(id))");
                                urlConnection = (HttpURLConnection) urlTracks.openConnection();
                                urlConnection.setRequestProperty("Authorization", "Bearer " + authToken);
                                if(urlConnection.getResponseCode() != 200){
                                    throw new RuntimeException("Failed: HTTP error code "+urlConnection.getResponseCode());
                                }
                                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                                StringBuilder b = new StringBuilder();
                                while((input = in.readLine()) != null) {
                                    b.append(input);
                                }
                                JSONObject j = new JSONObject(b.toString());
                                JSONArray jArray = j.getJSONArray("items");
                                if (jArray != null) {
                                    for (int p=0;p<jArray.length();p++){
                                        id_songs.add(i+","+jArray.getString(p));
                                    }
                                }
                                Log.d("MainActivity", j.toString());
                            }
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
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