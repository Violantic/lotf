/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

import me.borawski.lotf.listener.PlayerListener;
import me.borawski.lotf.perk.InstaPerk;
import me.borawski.lotf.perk.PerkManager;
import me.borawski.lotf.perk.SpeedPerk;
import me.borawski.lotf.state.FinishState;
import me.borawski.lotf.state.LobbyState;
import me.borawski.lotf.state.PlayState;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Ethan on 5/5/2017.
 */
public class Minigame extends JavaPlugin {

    /**
     * Instance
     */
    private static Minigame instance;

    /**
     * Variables
     */
    private GameHandler handler;
    private PerkManager perkManager;

    /**
     * Register game states, as well as generic spigot stuff.
     * -> start game process
     */
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

        perkManager = new PerkManager(this);
        perkManager.register(new SpeedPerk());
        perkManager.register(new InstaPerk());

        getServer().getScheduler().runTaskTimer(this, handler, 0L, 20L);
    }

    /**
     * Instance
     * @return
     */
    public static Minigame getInstance() {
        return instance;
    }

    /**
     * Game Mechanic/State
     * @return
     */
    public GameHandler getHandler() {
        return handler;
    }

    /**
     * Perk Manager/Hub
     * @return
     */
    public PerkManager getPerkManager() {
        return perkManager;
    }
}
