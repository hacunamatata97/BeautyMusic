package com.badasspsycho.beautymusic.model.state.player;

import com.badasspsycho.beautymusic.controllers.manager.PlayerManager;

public class ShufflingState implements State {

    private PlayerManager playerManager;

    public ShufflingState(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void shuffle() {
        playerManager.setState(playerManager.getNormalState());
    }

    @Override
    public void repeat() {
        playerManager.setState(playerManager.getShuffleRepeatState());
    }
}
