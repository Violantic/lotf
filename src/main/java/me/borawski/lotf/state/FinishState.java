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

    private Minigame instance;

    public FinishState(Minigame instance) {
        this.instance = instance;
    }

    public Minigame getInstance() {
        return instance;
    }

    public String getId() {
        return "finish";
    }

    public String getDescription() {
        return "Game is over";
    }

    public void onStart() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            Location location = LocationUtil.getLocation("world", getInstance().getConfig().getString("spawn"));
            player.teleport(location);
        }
    }

    public void onFinish() {
        getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
            public void run() {
                for(Entity entity : Bukkit.getWorld("world").getEntities()) {
                    entity.remove();
                }
            }
        });

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Re-Log to play again!");
        }
        getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
    }

    public boolean canMove() {
        return true;
    }

    public boolean canAlterTerrain() {
        return false;
    }

    public boolean canPvP() {
        return false;
    }

    public boolean canPvE() {
        return false;
    }

    public boolean canTalk() {
        return true;
    }

    public int getLength() {
        return 20;
    }
}
