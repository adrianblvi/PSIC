package com.example.spotifylogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlaylists extends ArrayAdapter {
    private Context mContext;
    private List<String> playList;

    public AdapterPlaylists(Context context, ArrayList<String> list) {
        super(context,0,list);
        mContext = context;
        playList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spotify_playlists,parent,false);

        String currentPlaylist = playList.get(position);

        TextView name = listItem.findViewById(R.id.playlist_name);
        name.setText(currentPlaylist);

        return listItem;
    }
}
