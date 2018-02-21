package com.badasspsycho.beautymusic.model.state.player;

import com.badasspsycho.beautymusic.controllers.manager.PlayerManager;

public class RepeatingState implements State {

    private PlayerManager playerManager;

    public RepeatingState(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void shuffle() {
        playerManager.setState(playerManager.getShuffleRepeatState());
    }

    @Override
    public void repeat() {
        playerManager.setState(playerManager.getNormalState());
    }
}
