package com.example.spotifylogin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NewPlaylist extends AppCompatActivity {

    SpotifyLogin spotifyLogin = new SpotifyLogin();
    String filename = "play_lists.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_playlist);
        ArrayList<ListItem> songsPlayList = spotifyLogin.readFile(this);

        songsPlayList.sort((l1, l2) -> l1.getTitle().compareTo(l2.getTitle()));

        ListView listview = findViewById(R.id.listViewPlayList);
        AdapterSongs adapterSongs = new AdapterSongs(this, songsPlayList);
        listview.setAdapter(adapterSongs);

        Button btnRemoveAll = findViewById(R.id.remove_button);
        Button btnConfirm = findViewById(R.id.confirm_button);

        btnRemoveAll.setOnClickListener(v -> removePlaylist(filename, adapterSongs, songsPlayList,btnRemoveAll,btnConfirm));
        btnConfirm.setOnClickListener(v -> {
            btnRemoveAll.setVisibility(View.INVISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);

        });

        listview.setOnItemClickListener((parent2, view2, position2, id2)-> {

            if (btnRemoveAll.getVisibility() == View.VISIBLE){
                songsPlayList.remove(position2);
                adapterSongs.notifyDataSetChanged();
                spotifyLogin.overwriteFile(this, filename);
                writePlayList(this, filename, songsPlayList);
            }
        });

        listview.setOnItemLongClickListener((parent1, view1, position1, id1) -> {
            btnRemoveAll.setVisibility(View.VISIBLE);
            btnConfirm.setVisibility(View.VISIBLE);
            return true;
        });
    }

    public void removePlaylist(String filename,ArrayAdapter adapterSongs,ArrayList<ListItem> list,Button btnRemoveAll,Button btnConfirm){
        AlertDialog.Builder message = new AlertDialog.Builder(this);
        message.setMessage("Are you sure to clear the playlist?\nAll items will be removed");
        message.setCancelable(false);
        message.setPositiveButton("OK", (message1, id1) -> {
            list.removeAll(list);
            spotifyLogin.overwriteFile(this,filename);
            adapterSongs.notifyDataSetChanged();
            btnRemoveAll.setVisibility(View.INVISIBLE);
            btnConfirm.setVisibility(View.INVISIBLE);
        });
        message.setNegativeButton("Cancel", (message2, id2) ->{
        });
        message.show();
    }
    public void writePlayList(Context context,String filename,ArrayList <ListItem> listPlayList){

        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_APPEND)) {
            if(listPlayList.size()!=0){
                for (int i =0;i<listPlayList.size();i++)  fos.write((listPlayList.get(i).getTitle() + "," + listPlayList.get(i).getArtist() + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}