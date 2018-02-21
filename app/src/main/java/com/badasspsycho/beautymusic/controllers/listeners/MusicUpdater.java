package com.badasspsycho.beautymusic.controllers.listeners;

import com.badasspsycho.beautymusic.model.entities.Album;
import com.badasspsycho.beautymusic.model.entities.Artist;
import com.badasspsycho.beautymusic.model.entities.Song;
import java.util.ArrayList;

public interface MusicUpdater {

    void updateListSong(ArrayList<Song> songList);

    void updateListAlbum(ArrayList<Album> albumList);

    void updateListArtist(ArrayList<Artist> artistList);
}
