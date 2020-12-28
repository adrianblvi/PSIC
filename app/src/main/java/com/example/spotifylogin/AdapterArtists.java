package com.example.spotifylogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterArtists extends ArrayAdapter<ListItem> {

    private final Context Context;
    private final List<ListItem> songsList;

    public AdapterArtists(Context context, ArrayList<ListItem> list) {
        super(context, 0 , list);
        Context = context;
        songsList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(Context).inflate(R.layout.list_item,parent,false);

        ListItem currentListItem = songsList.get(position);

        TextView title = listItem.findViewById(R.id.textView_title);
        title.setText(currentListItem.getTitle());

        /*TextView artist = listItem.findViewById(R.id.textView_artist);
        artist.setText(currentListItem.getArtist());*/

        return listItem;
    }
}

