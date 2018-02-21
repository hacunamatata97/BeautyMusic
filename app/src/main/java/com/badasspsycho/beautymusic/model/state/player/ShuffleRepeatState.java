package com.badasspsycho.beautymusic.model.state.player;

import com.badasspsycho.beautymusic.controllers.manager.PlayerManager;

public class ShuffleRepeatState implements State {

    private PlayerManager playerManager;

    public ShuffleRepeatState(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void shuffle() {
        playerManager.setState(playerManager.getRepeatingState());
    }

    @Override
    public void repeat() {
        playerManager.setState(playerManager.getShufflingState());
    }
}
