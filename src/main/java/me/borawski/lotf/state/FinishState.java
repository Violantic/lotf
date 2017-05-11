/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.state;

import me.borawski.lotf.GameState;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Ethan on 5/8/2017.
 */
public class FinishState implements GameState {

    /**
     * Instance
     */
    private Minigame instance;

    /**
     * const
     * @param instance
     */
    public FinishState(Minigame instance) {
        this.instance = instance;
    }

    /**
     * Instance
     * @return
     */
    public Minigame getInstance() {
        return instance;
    }

    /**
     * Variables
     */

    public String getId() {
        return "finish";
    }

    public String getDescription() {
        return "Game is over";
    }

    /**
     * Teleport players to pig(slaughter) house
     */
    public void onStart() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            Location location = LocationUtil.getLocation("world", getInstance().getConfig().getString("spawn"));
            player.teleport(location);
        }
    }

    /**
     * Kick all players, stop server and let instance system do its magic ;)
     */
    public void onFinish() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Re-Log to play again!");
        }
        getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }

    /**
     * Players can move
     * @return
     */
    public boolean canMove() {
        return true;
    }

    /**
     * Players can't edit blocks
     * @return
     */
    public boolean canAlterTerrain() {
        return false;
    }

    /**
     * Players can't PVP
     * @return
     */
    public boolean canPvP() {
        return false;
    }

    /**
     * Players can't PVE
     * @return
     */
    public boolean canPvE() {
        return false;
    }

    /**
     * Players can talk
     * @return
     */
    public boolean canTalk() {
        return true;
    }

    /**
     * 20 seconds
     * @return
     */
    public int getLength() {
        return 20;
    }
}
