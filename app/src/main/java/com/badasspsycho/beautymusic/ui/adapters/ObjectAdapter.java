package com.badasspsycho.beautymusic.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;

public class ObjectAdapter extends RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder> {

    private ArrayList<Object> objectList;

    public ObjectAdapter(ArrayList<Object> objectList) {
        this.objectList = objectList;
    }

    public ArrayList<Object> getObjectList() {
        return objectList;
    }

    public void setObjectList(ArrayList<Object> objectList) {
        this.objectList = objectList;
    }

    @Override
    public ObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_obj, parent, false);
        return new ObjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ObjectViewHolder holder, int position) {
        Object object = objectList.get(position);

        if (object instanceof Song) {
            Song song = (Song) object;
            holder.tvObjectName.setText(song.getTitle());
            holder.ivObjectImage.setImageResource(R.drawable.ic_beats_icon);
        } else if (object instanceof Album) {
            Album album = (Album) object;
            holder.tvObjectName.setText(album.getAlbumName());
            holder.ivObjectImage.setImageResource(R.drawable.ic_album_black_24dp);
        } else if (object instanceof Artist) {
            Artist artist = (Artist) object;
            holder.tvObjectName.setText(artist.getArtistName());
            holder.ivObjectImage.setImageResource(R.drawable.ic_headset_icon);
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ObjectViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivObjectImage;
        private TextView tvObjectName;

        ObjectViewHolder(View itemView) {
            super(itemView);
            ivObjectImage = itemView.findViewById(R.id.iv_object_icon);
            tvObjectName = itemView.findViewById(R.id.tv_object_name);
        }
    }
}
