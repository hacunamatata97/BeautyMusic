package com.badasspsycho.beautymusic.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private ArrayList<Song> songList;
    private boolean isOrdering = false;
    private int currentSongIndex = -1;

    public SongAdapter(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    public void setOrdering(boolean ordering) {
        this.isOrdering = ordering;
        notifyDataSetChanged();
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.tvTitle.setText(songList.get(position).getTitle());
        holder.tvArtist.setText(songList.get(position).getArtist());

        if (isOrdering) {
            holder.ivOrderIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.ivOrderIndicator.setVisibility(View.GONE);
        }

        if (holder.getAdapterPosition() == currentSongIndex) {
            holder.ivPlayingIndicator.setVisibility(View.VISIBLE);
            holder.tvTitle.setTextColor(holder.tvTitle.getResources().getColor(R.color.red));
        } else {
            holder.ivPlayingIndicator.setVisibility(View.GONE);
            holder.tvTitle.setTextColor(
                    holder.tvTitle.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return (songList != null) ? songList.size() : 0;
    }

    class SongViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView ivOrderIndicator;
        private ImageView ivPlayingIndicator;

        SongViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_song_artist);
            ivOrderIndicator = itemView.findViewById(R.id.iv_order_indicator);
            ivPlayingIndicator = itemView.findViewById(R.id.iv_playing_indicator);
        }
    }
}
