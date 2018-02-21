package com.badasspsycho.beautymusic.controllers.manager;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.badasspsycho.beautymusic.controllers.listeners.PlayerNotifier;
import com.badasspsycho.beautymusic.controllers.listeners.PlayerUpdater;
import com.badasspsycho.beautymusic.model.entities.Song;
import com.badasspsycho.beautymusic.model.state.player.NormalState;
import com.badasspsycho.beautymusic.model.state.player.RepeatingState;
import com.badasspsycho.beautymusic.model.state.player.ShuffleRepeatState;
import com.badasspsycho.beautymusic.model.state.player.ShufflingState;
import com.badasspsycho.beautymusic.model.state.player.State;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PlayerManager extends Service implements State, PlayerNotifier {

    private State state;
    private State normalState;
    private State shufflingState;
    private State repeatingState;
    private State shuffleRepeatState;

    private int currentSongIndex;
    private ArrayList<Song> songList;
    private ArrayList<PlayerUpdater> playerUpdaterList;

    private MediaPlayer mMediaPlayer;

    int playingProgress;
    int playingForProgressBar;

    public PlayerManager() {

        // Init player states
        normalState = new NormalState(this);
        shufflingState = new ShufflingState(this);
        repeatingState = new RepeatingState(this);
        shuffleRepeatState = new ShuffleRepeatState(this);
        state = normalState; // First state

        // Init song components
        currentSongIndex = 0;
        songList = new ArrayList<>();
        playerUpdaterList = new ArrayList<>();

        // Init Media Player
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener((mediaPlayer) -> next());

        // Init playing Progress
        playingProgress = 0;
        playingForProgressBar = 0;

        // Init ProgressBar
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer.isPlaying()) {
                    // Playing progress
                    int progress = playingProgress;

                    try {
                        progress = mMediaPlayer.getCurrentPosition() / 1000;
                    } catch (Exception ignored) {
                    }

                    if (progress != playingProgress) {
                        playingProgress = progress;
                        notifyPlayingProgressChanged(playingProgress);
                    }

                    // ProgressBar
                    int progressBar = playingForProgressBar;
                    try {
                        progressBar = mMediaPlayer.getCurrentPosition();
                    } catch (Exception ignored) {
                    }
                    if (progressBar != playingForProgressBar) {
                        playingForProgressBar = progressBar;
                        notifyPlayingForProgressBar(playingForProgressBar,
                                mMediaPlayer.getDuration());
                    }
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void setUpLastState(ArrayList<Song> songList, int lastIndex, int lastProgress) {
        this.songList = songList;
        this.currentSongIndex = lastIndex;

        try {
            mMediaPlayer.setDataSource(songList.get(currentSongIndex).getUri());
            mMediaPlayer.prepare();
            mMediaPlayer.seekTo(lastProgress);

            notifyPlayingProgressChanged(lastProgress / 1000);
            notifyPlayingForProgressBar(lastProgress, mMediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void seek(int percent) {
        try {
            int seekTo = mMediaPlayer.getDuration() * percent / 100;
            mMediaPlayer.seekTo(seekTo);
        } catch (Exception ignored) {

        }
    }

    public void shuffleHandle() {
        state.shuffle();
        notifyPlayManagerStateChanged();
    }

    public void repeatOneHandle() {
        state.repeat();
        notifyPlayManagerStateChanged();
    }

    public void playingStateHandle() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else if (!mMediaPlayer.isPlaying()) {
            try {
                mMediaPlayer.start();
            } catch (Exception ignored) {

            }
        }

        notifyPlayingStateChanged();
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            notifyPlayingStateChanged();
        }
    }

    public void play() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.start();
                notifyPlayingStateChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void next() {
        if (state instanceof NormalState) {
            currentSongIndex = (currentSongIndex == songList.size() - 1) ? 0 : currentSongIndex + 1;
        } else if (state instanceof ShufflingState) {
            currentSongIndex = new Random().nextInt(songList.size());
        }

        startNew();
    }

    public void prev() {
        if (state instanceof NormalState) {
            currentSongIndex = (currentSongIndex == 0) ? songList.size() - 1 : currentSongIndex - 1;
        } else if (state instanceof ShufflingState) {
            currentSongIndex = new Random().nextInt(songList.size());
        }

        startNew();
    }

    public void startNew() {
        if (songList.size() > 0) {
            mMediaPlayer.reset();

            try {
                mMediaPlayer.setDataSource(songList.get(currentSongIndex).getUri());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            notifySongChanged();
            notifyPlayingStateChanged();
        }
    }

    public void updateSongList(ArrayList<Song> songList, int currentSongIndex) {
        this.songList = songList;
        this.currentSongIndex = currentSongIndex;
        startNew();
    }

    public void updateSong(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
        startNew();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SongBinder();
    }

    public class SongBinder extends Binder {
        public PlayerManager getPlayerService() {
            return PlayerManager.this;
        }
    }

    @Override
    public void shuffle() {
        state.shuffle();
    }

    @Override
    public void repeat() {
        state.repeat();
    }

    @Override
    public void register(PlayerUpdater playerUpdater) {
        playerUpdaterList.add(playerUpdater);
    }

    @Override
    public void notifySongChanged() {
        for (PlayerUpdater playerUpdater : playerUpdaterList) {
            playerUpdater.updateSong(songList.get(currentSongIndex), currentSongIndex);
        }
    }

    @Override
    public void notifyPlayingStateChanged() {
        for (PlayerUpdater playerUpdater : playerUpdaterList) {
            playerUpdater.updatePlayingState(mMediaPlayer.isPlaying());
        }
    }

    @Override
    public void notifyPlayManagerStateChanged() {
        for (PlayerUpdater playerUpdater : playerUpdaterList) {
            playerUpdater.updatePlayManagerState(state);
        }
    }

    @Override
    public void notifyPlayingProgressChanged(int progress) {
        for (PlayerUpdater playerUpdater : playerUpdaterList) {
            playerUpdater.updatePlayingProgress(progress);
        }
    }

    @Override
    public void notifyPlayingForProgressBar(int progress, int duration) {
        for (PlayerUpdater playerUpdater : playerUpdaterList) {
            playerUpdater.updateForProgressBar(progress, duration);
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    public State getNormalState() {
        return normalState;
    }

    public State getShufflingState() {
        return shufflingState;
    }

    public State getRepeatingState() {
        return repeatingState;
    }

    public State getShuffleRepeatState() {
        return shuffleRepeatState;
    }
}
