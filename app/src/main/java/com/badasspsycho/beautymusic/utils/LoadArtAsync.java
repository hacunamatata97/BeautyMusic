package com.badasspsycho.beautymusic.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class LoadArtAsync extends AsyncTask<String, Void, String> {

    private Album album;
    private Artist artist;
    private ArrayList<Song> songList;

    private Bitmap art;

    private AtomicReference<ImageView> imageHolder = new AtomicReference<>();

    public LoadArtAsync(ArrayList<Song> songs, ImageView imageView) {
        this.songList = songs;
        this.imageHolder.set(imageView);
    }

    @Override
    protected String doInBackground(String... strings) {

        for (Song song : songList) {
            Bitmap bitmap = MusicLoader.getArt(song);

            if (bitmap != null) {
                this.art = bitmap;
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (art != null) {
            if (imageHolder.get() != null) imageHolder.get().setImageBitmap(art);

            if (album != null) {
                album.setAlbumArt(art);
            } else if (artist != null) {
                artist.setArtistArt(art);
            }
        }
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
