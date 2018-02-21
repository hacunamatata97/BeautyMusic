package com.badasspsycho.beautymusic.ui.adapters;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.utils.LoadArtAsync;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private ArrayList<Album> albumList;
    private boolean isGrid = true;

    public AlbumAdapter(ArrayList<Album> albumList) {
        this.albumList = albumList;
    }

    public void setAlbumList(ArrayList<Album> albumList) {
        this.albumList = albumList;
        notifyDataSetChanged();
    }

    public void setGrid(boolean grid) {
        isGrid = grid;
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {

        Album album = albumList.get(position);

        holder.ivAlbumArt.setImageDrawable(holder.ivAlbumArt.getContext()
                .getResources()
                .getDrawable(R.drawable.ic_album_black_24dp));

        holder.tvAlbumName.setText(album.getAlbumName());
        holder.tvAlbumArtist.setText(album.getSongList().get(0).getArtist());

        if (album.getAlbumArt() != null) {
            holder.ivAlbumArt.setImageBitmap(album.getAlbumArt());
            return;
        }

        LoadArtAsync loadArtAsync = new LoadArtAsync(album.getSongList(), holder.ivAlbumArt);
        loadArtAsync.setAlbum(album);
        loadArtAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getItemViewType(int position) {
        return isGrid ? R.layout.item_album : R.layout.item_album_list;
    }

    @Override
    public int getItemCount() {
        return albumList != null ? albumList.size() : 0;
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAlbumName;
        private TextView tvAlbumArtist;
        private ImageView ivAlbumArt;

        AlbumViewHolder(View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            tvAlbumArtist = itemView.findViewById(R.id.tv_album_artist);
            ivAlbumArt = itemView.findViewById(R.id.iv_album_art);
        }
    }
}
