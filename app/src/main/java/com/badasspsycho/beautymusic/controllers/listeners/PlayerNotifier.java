package com.badasspsycho.beautymusic.controllers.listeners;

public interface PlayerNotifier {

    void register(PlayerUpdater playerUpdater);

    void notifySongChanged();

    void notifyPlayingStateChanged();

    void notifyPlayManagerStateChanged();

    void notifyPlayingProgressChanged(int progress);

    void notifyPlayingForProgressBar(int progress, int duration);
}
