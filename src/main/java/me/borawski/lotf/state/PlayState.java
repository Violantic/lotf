/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.state;

import me.borawski.lotf.GameHandler;
import me.borawski.lotf.GameState;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.entity.CustomEntities;
import me.borawski.lotf.entity.EvilPig;
import me.borawski.lotf.util.ChatUtil;
import me.borawski.lotf.util.LocationUtil;
import net.minecraft.server.v1_11_R1.DamageSource;
import net.minecraft.server.v1_11_R1.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

/**
 * Created by Ethan on 5/8/2017.
 */
public class PlayState implements GameState {

    /**
     * Variables
     */
    private Minigame instance;
    private int pigSpawningTaskID = 0;
    public static int pigsAlive = 0;
    public static List<EvilPig> pigs = new ArrayList<EvilPig>();

    /**
     * const
     * @param instance
     */
    public PlayState(Minigame instance) {
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

    public static List<EvilPig> getPigs() {
        return pigs;
    }

    public String getId() {
        return "play";
    }

    public String getDescription() {
        return "Kill the pigs! Beware of evil mutations...";
    }

    /**
     * Notify the players of start.
     * Handles creation and deletion of custom entity spawning task.
     */
    public void onStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "LORD OF THE FLIES");
            player.sendMessage("");
            ChatUtil.sendCenteredMessage(player, ChatColor.AQUA + "The game can be won 2 ways:");
            ChatUtil.sendCenteredMessage(player, ChatColor.AQUA + "1. " + ChatColor.YELLOW + "Have the most points at the end");
            ChatUtil.sendCenteredMessage(player, ChatColor.AQUA + "2. " + ChatColor.YELLOW + "Survive the longest");
            ChatUtil.sendCenteredMessage(player, ChatColor.GRAY + "(Game Starting in 10 Seconds)");
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        }
        final BukkitTask task = getInstance().getServer().getScheduler().runTaskTimer(getInstance(), new Runnable() {
            boolean pigSpawning = false;
            int countdown = 10;
            double evilChance = 0.10;

            public void run() {
                if (countdown == 10 && !pigSpawning) {
                    countdown = 0;
                } else if(countdown == 0 && !pigSpawning) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 1);
                        player.sendMessage(ChatColor.YELLOW + "5 Seconds...");
                        apply(player);
                    }
                    pigSpawning = true;
                    getInstance().getPerkManager().startCycle();
                } else if(pigSpawning){
                    for (int i = 0; i < 9; i++) {
                        if (pigsAlive >= 14) {
                            return;
                        }
                        Location location = LocationUtil.getPig(i);
                        // Spawn Pig. //
                        Random r = new Random();
                        float chance = r.nextFloat();
                        if (chance <= 0.05) {
                            EvilPig pig = new EvilPig(location.getWorld());
                            pig.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "BEAST");
                            CustomEntities.spawnEntity(pig, location);
                            pigs.add(pig);
                        } else if (chance <= evilChance) {
                            EvilPig pig = new EvilPig(location.getWorld());
                            pig.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "BACON");
                            CustomEntities.spawnEntity(pig, location);
                            pigs.add(pig);
                            //Pig e = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
                            //e.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "BACON");
                            //pigs.add(e);
                        } else {
                            EvilPig pig = new EvilPig(location.getWorld());
                            pig.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + "PIG");
                            CustomEntities.spawnEntity(pig, location);
                            pigs.add(pig);
                            //Pig e = (Pig) location.getWorld().spawnEntity(location, EntityType.PIG);
                            //e.setCustomName(ChatColor.YELLOW + "PIG");
                            //pigs.add(e);
                        }

                        pigsAlive++;
                    }
                }
            }
        }, 0L, 20L * 5L);
        pigSpawningTaskID = task.getTaskId();
    }

    /**
     * Handles deletion of pig spawning task, also displays the end results.
     */
    public void onFinish() {
        getInstance().getServer().getScheduler().cancelTask(pigSpawningTaskID);

        getInstance().getServer().getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {
            public void run() {
                for(EvilPig pig : pigs) {
                    pig.damageEntity(DamageSource.OUT_OF_WORLD, 20);
                }
            }
        });

        UUID winner = null,second=null,third = null;

        try {
            winner = getInstance().getHandler().getTopThree().get(0);
            second = getInstance().getHandler().getTopThree().get(1);
            third = getInstance().getHandler().getTopThree().get(2);
        } catch (Exception e) {

        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
            ChatUtil.sendCenteredMessage(player, ChatColor.RED + "" + ChatColor.BOLD + "LORD OF THE FLIES");
            ChatUtil.sendCenteredMessage(player, ChatColor.YELLOW + (winner==null?"N/A":Bukkit.getPlayer(winner).getName()) + " has won the game!");
            ChatUtil.sendCenteredMessage(player, ChatColor.AQUA + "" + "#1 " + (winner==null?"N/A":Bukkit.getPlayer(winner).getName() + ChatColor.GRAY + " with " + getScore(winner) + " points"));
            ChatUtil.sendCenteredMessage(player, ChatColor.GREEN + "" + "#2 " + (second==null?"N/A":Bukkit.getPlayer(second).getName() + ChatColor.GRAY + " with " + getScore(second) + " points"));
            ChatUtil.sendCenteredMessage(player, ChatColor.GRAY + "" + "#3 " + (third==null?"N/A":Bukkit.getPlayer(third).getName() + ChatColor.GRAY + " with " + getScore(second) + " points"));
            ChatUtil.sendCenteredMessage(player, ChatColor.GRAY + "(Server restarting in 20 seconds)");
            player.sendMessage(ChatColor.GREEN + "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        }
    }

    /**
     * Score
     * @param uuid
     * @return
     */
    public int getScore(UUID uuid) {
        return getInstance().getHandler().getScore().get(uuid);
    }

    /**
     * Player can move
     * @return
     */
    public boolean canMove() {
        return true;
    }

    /**
     * Player can't edit blocks
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
        return true;
    }

    /**
     * Players can talk
     * @return
     */
    public boolean canTalk() {
        return true;
    }

    /**
     * 1.5 Minutes
     * @return
     */
    public int getLength() {
        return 90;
    }

    /**
     * Handles setting players initial scoreboard, and has a runnable in which
     * updates the side display slot async
     * @param player
     */
    public static void apply(final Player player) {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        final Objective objective = scoreboard.registerNewObjective("GameSB", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        final org.bukkit.scoreboard.Team team8 = scoreboard.registerNewTeam("team8");
        final org.bukkit.scoreboard.Team team9 = scoreboard.registerNewTeam("team9");
        final org.bukkit.scoreboard.Team team10 = scoreboard.registerNewTeam("team10");
        final org.bukkit.scoreboard.Team team11 = scoreboard.registerNewTeam("team11");
        final org.bukkit.scoreboard.Team team12 = scoreboard.registerNewTeam("team12");
        final org.bukkit.scoreboard.Team team13 = scoreboard.registerNewTeam("team13");

        team8.addPlayer(Bukkit.getOfflinePlayer(ChatColor.DARK_BLUE.toString()));
        team9.addPlayer(Bukkit.getOfflinePlayer(ChatColor.DARK_GRAY.toString()));
        team10.addPlayer(Bukkit.getOfflinePlayer(ChatColor.RED.toString()));
        team11.addPlayer(Bukkit.getOfflinePlayer(ChatColor.DARK_RED.toString()));
        team12.addPlayer(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString()));
        team13.addPlayer(Bukkit.getOfflinePlayer(ChatColor.GOLD.toString()));

        objective.getScore(ChatColor.DARK_BLUE.toString()).setScore(6);
        objective.getScore(ChatColor.DARK_GRAY.toString()).setScore(5);
        objective.getScore(ChatColor.RED.toString()).setScore(4);
        objective.getScore(ChatColor.DARK_RED.toString()).setScore(3);
        objective.getScore(ChatColor.GREEN.toString()).setScore(2);
        objective.getScore(ChatColor.GOLD.toString()).setScore(1);
        new BukkitRunnable() {
            public void run() {
                if (!Minigame.getInstance().getHandler().getCurrentState().getId().equalsIgnoreCase("play")) {
                    cancel();
                    return;
                }
                int you = Minigame.getInstance().getHandler().getScore().get(player.getUniqueId());
                int numberOfMinutes, numberOfSeconds;
                numberOfMinutes = (((90-Minigame.getInstance().getHandler().getSecond()) % 86400) % 3600) / 60;
                numberOfSeconds = (((90-Minigame.getInstance().getHandler().getSecond()) % 86400) % 3600) % 60;
                String seconds = ((numberOfSeconds < 10) ? ("0" + numberOfSeconds) : numberOfSeconds + "");
                final String minutes = ((numberOfMinutes < 10) ? ("0" + numberOfMinutes) + ":" : numberOfMinutes + ":") + seconds;

                objective.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "• LOTF •");
                team8.setPrefix("§a§lYour Score");
                team9.setPrefix(ChatColor.RED + "► " + you);
                team10.setPrefix("§a§lPlayers");
                team11.setPrefix(ChatColor.RED + "► " + Minigame.getInstance().getServer().getOnlinePlayers().size());
                team12.setPrefix("§a§lTime Left");
                team13.setPrefix(ChatColor.RED + "► " + minutes);
            }
        }.runTaskTimerAsynchronously(Minigame.getInstance(), 0L, 20L);

        player.setScoreboard(scoreboard);
    }
}
