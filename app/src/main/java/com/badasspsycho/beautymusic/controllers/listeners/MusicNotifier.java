package com.badasspsycho.beautymusic.controllers.listeners;

public interface MusicNotifier {

    void register(MusicUpdater musicUpdater);

    void notifyListSongChanged();

    void notifyListAlbumChanged();

    void notifyListArtistChanged();

}
