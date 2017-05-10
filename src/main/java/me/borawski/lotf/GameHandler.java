/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

import me.borawski.lotf.util.ChatUtil;
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

    private Minigame instance;
    private List<GameState> gameStateList;
    private volatile AtomicReference<Integer> second;
    private List<UUID> players;

    private boolean notifiedLobbyDelay = false;
    private boolean initiated = false;

    private Map<UUID, Integer> score;

    public static final Integer MINIMUM_PLAYERS = 3;
    public static final Integer MAXIMUM_PLAYERS = 8;

    public static Integer STATE_ID = 0;

    public GameHandler(Minigame instance) {
        this.instance = instance;
        this.gameStateList = new ArrayList<GameState>();
        this.second = new AtomicReference<Integer>();
        this.second.set(0);
        this.players = new ArrayList<UUID>();
        this.score = new ConcurrentHashMap<UUID, Integer>();
    }

    public Minigame getInstance() {
        return instance;
    }

    public List<GameState> getGameStateList() {
        return gameStateList;
    }

    public void registerState(GameState state) {
        getGameStateList().add(state);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void registerPlayer(Player player) {
        getPlayers().add(player.getUniqueId());
        getScore().put(player.getUniqueId(), 0);
    }

    public void unregisterPlayer(Player player) {
        getPlayers().remove(player.getUniqueId());
    }

    public Integer getSecond() {
        return second.get();
    }

    private void incrementSecond() {
        second.set(second.get() + 1);

        if(STATE_ID != 0) return;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel((getCurrentState().getLength())-getSecond());
            player.setHealth(20D);
            player.setFoodLevel(20);
        }
    }

    public int onlinePlayers() {
        return getInstance().getServer().getOnlinePlayers().size();
    }

    public GameState getCurrentState() {
        return getGameStateList().get(STATE_ID);
    }

    public List<UUID> getTopThree() {
        UUID winner = null;
        UUID second = null;
        UUID third = null;
        for (Map.Entry<UUID, Integer> player : getScore().entrySet()) {
            if (winner == null || (player.getValue() >= getScore().get(winner))) {
                winner = player.getKey();
            } else if (second == null || (player.getValue() >= getScore().get(second))) {
                second = player.getKey();
            } else if (third == null || (player.getValue() >= getScore().get(third))) {
                third = player.getKey();
            }
        }

        final UUID finalWinner = winner;
        final UUID finalSecond = second;
        final UUID finalThird = third;
        return new ArrayList<UUID>() {
            {
                add(finalWinner);
                add(finalSecond);
                add(finalThird);
            }
        };
    }

    public void finishGame() {
        UUID winner = null;
        UUID second = null;
        UUID third = null;
        for (Map.Entry<UUID, Integer> player : getScore().entrySet()) {
            if (winner == null || (player.getValue() >= getScore().get(winner))) {
                winner = player.getKey();
            } else if (second == null || (player.getValue() >= getScore().get(second))) {
                second = player.getKey();
            } else if (third == null || (player.getValue() >= getScore().get(third))) {
                third = player.getKey();
            }
        }

        getInstance().getServer().broadcastMessage(ChatColor.AQUA + Bukkit.getPlayer(winner).getName() + ChatColor.YELLOW + " has won the game");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "LORD OF THE FLIES");
            player.sendMessage("");
            ChatUtil.sendCenteredMessage(player, ChatColor.AQUA + "" + ChatColor.BOLD + "► " + ChatColor.GRAY + Bukkit.getPlayer(winner).getName());
            ChatUtil.sendCenteredMessage(player, ChatColor.GREEN + "" + ChatColor.BOLD + "► " + ChatColor.GRAY + Bukkit.getPlayer(second).getName());
            ChatUtil.sendCenteredMessage(player, ChatColor.GRAY + "" + ChatColor.BOLD + "► " + ChatColor.GRAY + Bukkit.getPlayer(third).getName());
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        }
    }

    public Map<UUID, Integer> getScore() {
        return score;
    }

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
