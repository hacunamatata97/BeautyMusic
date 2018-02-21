package com.badasspsycho.beautymusic.controllers.listeners;

import com.badasspsycho.beautymusic.model.entities.Song;
import com.badasspsycho.beautymusic.model.state.player.State;

public interface PlayerUpdater {

    void updateSong(Song song, int index);

    void updatePlayingState(boolean isPlaying);

    void updatePlayManagerState(State state);

    void updatePlayingProgress(int progress);

    void updateForProgressBar(int progress, int duration);
}
