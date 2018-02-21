package com.badasspsycho.beautymusic.model.entities;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Album {

    private String albumName;
    private ArrayList<Song> songList;
    private Bitmap albumArt;

    public Album(String albumName) {
        this.albumName = albumName;
        songList = new ArrayList<>();
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    @Override
    public String toString() {
        return albumName + "\n";
    }
}
