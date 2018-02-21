package com.badasspsycho.beautymusic.model.state.player;

import com.badasspsycho.beautymusic.controllers.manager.PlayerManager;

public class NormalState implements State {

    private PlayerManager playerManager;

    public NormalState(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void shuffle() {
        playerManager.setState(playerManager.getShufflingState());
    }

    @Override
    public void repeat() {
        playerManager.setState(playerManager.getRepeatingState());
    }
}
