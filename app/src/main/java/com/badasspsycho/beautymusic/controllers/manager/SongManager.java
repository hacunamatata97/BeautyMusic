package com.badasspsycho.beautymusic.controllers.manager;

import com.badasspsycho.beautymusic.controllers.listeners.MusicNotifier;
import com.badasspsycho.beautymusic.controllers.listeners.MusicUpdater;
import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;

public class SongManager implements MusicNotifier {

    private ArrayList<Song> songList;
    private ArrayList<Album> albumList;
    private ArrayList<Artist> artistList;

    private ArrayList<MusicUpdater> musicUpdaterList;

    public SongManager() {
        musicUpdaterList = new ArrayList<>();
    }

    @Override
    public void register(MusicUpdater musicUpdater) {
        musicUpdaterList.add(musicUpdater);
    }

    @Override
    public void notifyListSongChanged() {
        for (MusicUpdater musicUpdater : musicUpdaterList) {
            musicUpdater.updateListSong(songList);
        }
    }

    @Override
    public void notifyListAlbumChanged() {
        for (MusicUpdater musicUpdater : musicUpdaterList) {
            musicUpdater.updateListAlbum(albumList);
        }
    }

    @Override
    public void notifyListArtistChanged() {
        for (MusicUpdater musicUpdater : musicUpdaterList) {
            musicUpdater.updateListArtist(artistList);
        }
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public ArrayList<Album> getAlbumList() {
        return albumList;
    }

    public ArrayList<Artist> getArtistList() {
        return artistList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
        notifyListSongChanged();
    }

    public void setAlbumList(ArrayList<Album> albumList) {

        for (int i = 0; i < albumList.size(); i++) {
            if (i != 0) albumList.get(i).getSongList().remove(0);
        }

        this.albumList = albumList;
        notifyListAlbumChanged();
    }

    public void setArtistList(ArrayList<Artist> artistList) {

        for (int i = 0; i < artistList.size(); i++) {
            if (i != 0) artistList.get(i).getSongList().remove(0);
        }

        this.artistList = artistList;
        notifyListArtistChanged();
    }
}
