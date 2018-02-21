package com.badasspsycho.beautymusic.model.entities;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Artist {

    private String artistName;
    private ArrayList<Song> songList;
    private Bitmap artistArt;

    public Artist(String artistName) {
        this.artistName = artistName;
        songList = new ArrayList<>();
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public Bitmap getArtistArt() {
        return artistArt;
    }

    public void setArtistArt(Bitmap artistArt) {
        this.artistArt = artistArt;
    }

    @Override
    public String toString() {
        return artistName + "\n";
    }
}
