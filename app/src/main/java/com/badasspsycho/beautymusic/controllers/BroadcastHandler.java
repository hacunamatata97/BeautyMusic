package com.badasspsycho.beautymusic.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.badasspsycho.beautymusic.controllers.manager.NotiManager;
import com.badasspsycho.beautymusic.ui.activities.MainActivity;

public class BroadcastHandler extends BroadcastReceiver {

    private static boolean LAST_STATE_BY_PHONE = false;
    private static boolean LAST_STATE_BY_HEADSET = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;

        if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            KeyEvent keyEvent = (KeyEvent) bundle.get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) return;

            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                MainActivity.playerManager.playingStateHandle();
            }
            return;
        }

        // Detect incoming call
        if (action.equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra("state");
            if (MainActivity.playerManager != null) {
                if (state.equals("RINGING") || state.equals("OFFHOOK")) {
                    LAST_STATE_BY_PHONE = MainActivity.playerManager.isPlaying();
                    MainActivity.playerManager.pause();
                } else if (state.equals("IDLE") && LAST_STATE_BY_PHONE) {
                    MainActivity.playerManager.play();
                }
            }
            return;
        }

        // Detect headset plugged
        if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
            int status = intent.getIntExtra("state", -1);
            if (status == 0 && MainActivity.playerManager != null) {
                LAST_STATE_BY_HEADSET = MainActivity.playerManager.isPlaying();
                MainActivity.playerManager.pause();
            } else if (status == 1
                    && MainActivity.playerManager != null
                    && LAST_STATE_BY_HEADSET) {
                MainActivity.playerManager.play();
            }
            return;
        }

        if (action.equals(NotiManager.ACTION_PLAY)) {
            if (MainActivity.playerManager != null) {
                MainActivity.playerManager.playingStateHandle();
            }
            return;
        }

        if (action.equals(NotiManager.ACTION_NEXT)) {
            if (MainActivity.playerManager != null) {
                MainActivity.playerManager.next();
            }
            return;
        }

        if (action.equals(NotiManager.ACTION_PREV)) {
            if (MainActivity.playerManager != null) {
                MainActivity.playerManager.prev();
            }
        }
    }
}
