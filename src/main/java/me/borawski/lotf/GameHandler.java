/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

import me.borawski.lotf.util.ChatUtil;
import me.borawski.lotf.util.ScoreUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ethan on 5/5/2017.
 */
public class GameHandler implements Runnable {

    /**
     * Instance
     */
    private Minigame instance;

    /**
     * Variables
     */

    private List<GameState> gameStateList;
    private volatile AtomicReference<Integer> second;
    private List<UUID> players;
    // Static because we're only using it for 1 check in player listener. //
    public static List<UUID> instaKill;

    private boolean notifiedLobbyDelay = false;
    private boolean initiated = false;
    private Map<UUID, Integer> score;

    public static final Integer MINIMUM_PLAYERS = 1;
    public static final Integer MAXIMUM_PLAYERS = 8;

    public static Integer STATE_ID = 0;

    /**
     * Instantiate atomic reference, and set current second.
     * Also setup player cache and score cache.
     *
     * @param instance
     */
    public GameHandler(Minigame instance) {
        this.instance = instance;
        this.gameStateList = new ArrayList<GameState>();
        this.second = new AtomicReference<Integer>();
        this.second.set(0);
        this.players = new ArrayList<UUID>();
        instaKill = new ArrayList<UUID>();
        this.score = new ConcurrentHashMap<UUID, Integer>();
    }

    /**
     * Instance
     *
     * @return
     */
    public Minigame getInstance() {
        return instance;
    }

    /**
     * List of registered GameState's
     *
     * @return
     */
    public List<GameState> getGameStateList() {
        return gameStateList;
    }

    /**
     * Register a Game State
     *
     * @param state
     */
    public void registerState(GameState state) {
        getGameStateList().add(state);
    }

    /**
     * Get player cache
     *
     * @return
     */
    public List<UUID> getPlayers() {
        return players;
    }

    /**
     * Register a player
     *
     * @param player
     */
    public void registerPlayer(Player player) {
        getPlayers().add(player.getUniqueId());
        getScore().put(player.getUniqueId(), 0);
    }

    /**
     * Unregister a player
     *
     * @param player
     */
    public void unregisterPlayer(Player player) {
        getPlayers().remove(player.getUniqueId());
    }

    /**
     * Get the current second of the sequence interval
     *
     * @return
     */
    public Integer getSecond() {
        return second.get();
    }

    /**
     * Increment second, also do other cool things to the player
     */
    private void incrementSecond() {
        second.set(second.get() + 1);

        if (STATE_ID != 0) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel((getCurrentState().getLength()) - getSecond());
            player.setHealth(20D);
            player.setFoodLevel(20);
        }
    }

    /**
     * How many players are online
     *
     * @return
     */
    public int onlinePlayers() {
        return getInstance().getServer().getOnlinePlayers().size();
    }

    /**
     * Current GameState for sequence interval
     *
     * @return
     */
    public GameState getCurrentState() {
        return getGameStateList().get(STATE_ID);
    }

    /**
     * Get score list for top 3 players.
     * TODO: Broken 3rd place result
     *
     * @return
     */
    public List<UUID> getTopThree() {
        return ScoreUtil.topThree();
    }

    /**
     * End the game.
     * (Cosmetically, not mechanically)
     */
    public void finishGame() {
    }

    /**
     * Score map
     *
     * @return
     */
    public Map<UUID, Integer> getScore() {
        return score;
    }

    /**
     * Runs every second, checks for simple gamestate mechanics, and handles the lobby.
     * Supports any addition of new GameState's after the lobby state.
     */
    public void run() {
        if (onlinePlayers() < MINIMUM_PLAYERS && STATE_ID == 0) {
            second.set(0);
            return;
        } else if (onlinePlayers() >= MINIMUM_PLAYERS && STATE_ID == 0 && initiated && getSecond() >= gameStateList.get(0).getLength()) {
            second.set(0);
            getCurrentState().onFinish();
            STATE_ID++;
            getCurrentState().onStart();
            return;
        } else if (onlinePlayers() >= MINIMUM_PLAYERS && STATE_ID == 0 && getSecond() >= gameStateList.get(0).getLength()) {
            second.set(0);
            getCurrentState().onStart();
            initiated = true;
            return;
        } else if (getSecond() >= getCurrentState().getLength()) {
            getCurrentState().onFinish();
            second.set(0);
            if (STATE_ID >= getGameStateList().size()) {
                // Game has finished. //
                finishGame();
            } else if (STATE_ID < getGameStateList().size()) {
                if (getCurrentState().getId().equalsIgnoreCase("lobby") && onlinePlayers() < MINIMUM_PLAYERS) {
                    if (!notifiedLobbyDelay) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
                            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "A player left your lobby!");
                            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "Resuming when player requirements are met.");
                            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
                        }
                        notifiedLobbyDelay = true;
                    }
                    return;
                }
                STATE_ID++;
                getCurrentState().onStart();
            }
        }

        incrementSecond();
    }

}
