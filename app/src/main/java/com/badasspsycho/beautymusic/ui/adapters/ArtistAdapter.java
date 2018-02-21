package com.badasspsycho.beautymusic.ui.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.utils.LoadArtAsync;
import java.util.ArrayList;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private ArrayList<Artist> artistList;
    private boolean isGrid = true;

    public ArtistAdapter(ArrayList<Artist> artistList) {
        this.artistList = artistList;
    }

    public void setArtistList(ArrayList<Artist> artistList) {
        this.artistList = artistList;
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return isGrid ? R.layout.item_artist : R.layout.item_artist_list;
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);

        holder.ivArtistArt.setImageDrawable(holder.ivArtistArt.getContext()
                .getResources()
                .getDrawable(R.drawable.ic_headset_icon));

        holder.tvArtistName.setText(artist.getArtistName());

        int count = artist.getSongList().size();
        String detail = count == 1 ? "song" : "songs";
        holder.tvArtistDetail.setText(String.format(Locale.getDefault(), "%d %s", count, detail));

        if (artist.getArtistArt() != null) {
            holder.ivArtistArt.setImageBitmap(artist.getArtistArt());
            return;
        }

        LoadArtAsync loadArtAsync = new LoadArtAsync(artist.getSongList(), holder.ivArtistArt);
        loadArtAsync.setArtist(artist);
        loadArtAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getItemCount() {
        return artistList != null ? artistList.size() : 0;
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        private TextView tvArtistName;
        private TextView tvArtistDetail;
        private ImageView ivArtistArt;

        ArtistViewHolder(View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            tvArtistDetail = itemView.findViewById(R.id.tv_artist_detail);
            ivArtistArt = itemView.findViewById(R.id.iv_artist_art);
        }
    }
}
