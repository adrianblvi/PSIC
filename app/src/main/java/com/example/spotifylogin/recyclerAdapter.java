package com.example.spotifylogin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> implements Filterable {

    private ArrayList<ListItem> songList;
    private ArrayList<ListItem> originalItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public recyclerAdapter(ArrayList<ListItem> songList) {
        this.songList = songList;
        originalItems = new ArrayList<>(songList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_artist;


        public MyViewHolder(final View itemView, final OnItemClickListener listener) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.textView_title);
            tv_artist = itemView.findViewById(R.id.textView_artist);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
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
                filteredList.addAll(originalItems);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ListItem item : originalItems) {
                    if (item.getTitle().toLowerCase().startsWith(filterPattern)) {
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
            songList.clear();
            songList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    @NonNull
    @Override
    public recyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerAdapter.MyViewHolder holder, int position) {
        String title = songList.get(position).getTitle();
        String artist = songList.get(position).getArtist();
        holder.tv_title.setText(title);
        holder.tv_artist.setText(artist);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }
}
