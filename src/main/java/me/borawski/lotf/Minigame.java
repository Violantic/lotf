/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

import me.borawski.lotf.listener.PlayerListener;
import me.borawski.lotf.state.FinishState;
import me.borawski.lotf.state.LobbyState;
import me.borawski.lotf.state.PlayState;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan on 5/5/2017.
 */
public class Minigame extends JavaPlugin {

    private static Minigame instance;
    private GameHandler handler;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        handler = new GameHandler(this);
        handler.registerState(new LobbyState(this));
        handler.registerState(new PlayState(this));
        handler.registerState(new FinishState(this));

        GameHandler.STATE_ID = 0;

        getServer().getScheduler().runTaskTimer(this, handler, 0L, 20L);
    }

    public static Minigame getInstance() {
        return instance;
    }

    public GameHandler getHandler() {
        return handler;
    }
}
