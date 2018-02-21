package com.badasspsycho.beautymusic.controllers.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.badasspsycho.beautymusic.R;
import com.badasspsycho.beautymusic.controllers.listeners.PlayerUpdater;
import com.badasspsycho.beautymusic.model.entities.Song;
import com.badasspsycho.beautymusic.model.state.player.State;

public class NotiManager implements PlayerUpdater {

    public static final String ACTION_PLAY = "play";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "previous";
    private static final int KEY_NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    private Context mContext;

    private Song mSong;

    public NotiManager(Context context) {
        mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.mContext = context;
    }

    private void displayNotification(boolean isPlaying) {

        Intent playIntent = new Intent(ACTION_PLAY);
        playIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPlay = PendingIntent.getBroadcast(mContext, 0, playIntent, 0);

        Intent nextIntent = new Intent(ACTION_NEXT);
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNext = PendingIntent.getBroadcast(mContext, 0, nextIntent, 0);

        Intent prevIntent = new Intent(ACTION_PREV);
        nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingPrev = PendingIntent.getBroadcast(mContext, 0, prevIntent, 0);

        RemoteViews remoteViewsExpanded =
                new RemoteViews(mContext.getPackageName(), R.layout.notification_expanded_player);

        if (isPlaying) {
            remoteViewsExpanded.setImageViewResource(R.id.iv_notification_expanded_play_pause,
                    R.drawable.button_pause_white);
        } else {
            remoteViewsExpanded.setImageViewResource(R.id.iv_notification_expanded_play_pause,
                    R.drawable.button_play_white);
        }

        remoteViewsExpanded.setTextViewText(R.id.tv_notification_expanded_title, mSong.getTitle());
        remoteViewsExpanded.setTextViewText(R.id.tv_notification_expanded_album, mSong.getAlbum());
        remoteViewsExpanded.setTextViewText(R.id.tv_notification_expanded_artist,
                mSong.getArtist());

        remoteViewsExpanded.setOnClickPendingIntent(R.id.iv_notification_expanded_prev,
                pendingPrev);
        remoteViewsExpanded.setOnClickPendingIntent(R.id.iv_notification_expanded_next,
                pendingNext);
        remoteViewsExpanded.setOnClickPendingIntent(R.id.iv_notification_expanded_play_pause,
                pendingPlay);

        RemoteViews remoteViewsNormal =
                new RemoteViews(mContext.getPackageName(), R.layout.notification_player);
        if (isPlaying) {
            remoteViewsNormal.setImageViewResource(R.id.iv_notification_play_pause,
                    R.drawable.button_pause_white);
        } else {
            remoteViewsNormal.setImageViewResource(R.id.iv_notification_play_pause,
                    R.drawable.button_play_white);
        }
        remoteViewsNormal.setTextViewText(R.id.tv_notification_track_title, mSong.getTitle());
        remoteViewsNormal.setTextViewText(R.id.tv_notification_track_artist, mSong.getArtist());

        remoteViewsNormal.setOnClickPendingIntent(R.id.iv_notification_play_pause, pendingPlay);
        remoteViewsNormal.setOnClickPendingIntent(R.id.iv_notification_next, pendingNext);
        remoteViewsNormal.setOnClickPendingIntent(R.id.iv_notification_prev, pendingPrev);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext, "").setCustomBigContentView(
                        remoteViewsExpanded)
                        .setCustomContentView(remoteViewsNormal)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_beats_icon);

        if (isPlaying) {
            builder.setPriority(Notification.PRIORITY_MAX)
                    .setUsesChronometer(true)
                    .setOngoing(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        mNotificationManager.notify(KEY_NOTIFICATION_ID, builder.build());
    }

    @Override
    public void updateSong(Song song, int index) {
        mSong = song;
        displayNotification(true);
    }

    @Override
    public void updatePlayingState(boolean isPlaying) {
        if (mSong != null) displayNotification(isPlaying);
    }

    @Override
    public void updatePlayManagerState(State state) {

    }

    @Override
    public void updatePlayingProgress(int progress) {

    }

    @Override
    public void updateForProgressBar(int progress, int duration) {

    }

    public Song getSong() {
        return mSong;
    }

    public void setSong(Song song) {
        mSong = song;
    }
}
