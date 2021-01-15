package com.example.spotifylogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterArtists extends ArrayAdapter<ListItem> implements Filterable {

    private final Context Context;
    private List<ListItem> songsList;
    private List<ListItem> originalItem;


    public AdapterArtists(Context context, ArrayList<ListItem> list) {
        super(context, 0, list);
        Context = context;
        songsList = list;
        originalItem = new ArrayList<>(songsList);
    }


    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ListItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalItem);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ListItem item : originalItem
                ) {
                    if (item.getArtist().toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            songsList.clear();
            songsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(Context).inflate(R.layout.list_item, parent, false);

        ListItem currentListItem = songsList.get(position);

//        TextView title = listItem.findViewById(R.id.textView_title);
//        title.setText(currentListItem.getTitle());

        TextView artist = listItem.findViewById(R.id.textView_artist);
        artist.setText(currentListItem.getArtist());

        return listItem;
    }
}

