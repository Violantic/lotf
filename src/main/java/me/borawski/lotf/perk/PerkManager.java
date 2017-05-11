/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.perk;

import me.borawski.lotf.Minigame;
import me.borawski.lotf.util.BarUtil;
import me.borawski.lotf.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Ethan on 5/10/2017.
 */
public class PerkManager implements Listener, Runnable {

    private Minigame instance;
    private List<Perk> perks;
    boolean resetPerk = false;
    private AtomicReference<Object> currentItem;
    private List<UUID> usingPerk;
    private Perk currentPerk;

    public PerkManager(Minigame instance) {
        this.instance = instance;
        this.perks = new ArrayList<>();
        this.currentItem = new AtomicReference<>();
        this.usingPerk = new ArrayList<>();
        this.currentPerk = null;
        resetPerk = true;
        getInstance().getServer().getPluginManager().registerEvents(this, instance);
    }

    public Minigame getInstance() {
        return instance;
    }

    public List<Perk> getPerks() {
        return perks;
    }

    public void register(Perk p) {
        perks.add(p);
    }

    public List<UUID> getUsingPerk() {
        return usingPerk;
    }

    public Location getDrop() {
        return LocationUtil.getLocation("world", getInstance().getConfig().getString("drop"));
    }

    public void startCycle() {
        getInstance().getServer().getScheduler().runTaskTimer(getInstance(), this, 0L, 20L);
    }

    public void onSpawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BarUtil.sendActionBar(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "The " + ChatColor.AQUA + currentPerk.getName() + " Perk " + ChatColor.YELLOW + "has spawned!");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
        getInstance().getServer().getWorld("world").strikeLightningEffect(getDrop());
        resetPerk = false;
    }

    public void onPickup(Player picker, Perk perk) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BarUtil.sendActionBar(player, ChatColor.AQUA + "" + ChatColor.BOLD + picker.getName() + " " + ChatColor.YELLOW + "has picked up the " + ChatColor.AQUA + perk.getName() + " Perk" + ChatColor.YELLOW + "!");
            if (player != picker)
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
        }
        picker.playSound(picker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        getUsingPerk().add(picker.getUniqueId());
        resetPerk = true;
    }

    public void failedToPickup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BarUtil.sendActionBar(player, ChatColor.RED + "" + ChatColor.BOLD + "The next perk will not spawn until current one has been grabbed!");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        if (!getInstance().getHandler().getCurrentState().getId().equalsIgnoreCase("play")) return;

        event.setCancelled(true);
        event.getItem().remove();
        currentItem.set(null);
        onPickup(event.getPlayer(), currentPerk);
        currentPerk.playerConsume(event.getPlayer());
        currentPerk = null;
        resetPerk = true;
    }

    private int count = 30;
    @Override
    public void run() {
        if (count == 30) {
            if (!getInstance().getHandler().getCurrentState().getId().equalsIgnoreCase("play")) return;
            if (currentPerk == null) {
                Random random = new Random();
                int index = random.nextInt(getPerks().size());
                Perk current = getPerks().get(index);
                Item item = getInstance().getServer().getWorld("world").dropItem(getDrop(), current.getItem());
                item.setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + current.getName());
                currentItem.set(item);
                currentPerk = current;
                onSpawn();
                count--;
            } else {
                failedToPickup();
                count--;
            }
        } else if (count == 0) {
            count = 30;
        } else {
            count--;
        }
    }
}
