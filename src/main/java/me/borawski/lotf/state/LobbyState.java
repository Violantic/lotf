/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.state;

import me.borawski.lotf.GameState;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Ethan on 5/8/2017.
 */
public class LobbyState implements GameState {

    private Minigame instance;

    public LobbyState(Minigame instance) {
        this.instance = instance;
    }

    public Minigame getInstance() {
        return instance;
    }

    public String getId() {
        return "lobby";
    }

    public String getDescription() {
        return "Wait for the game to start";
    }

    public void onStart() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Starting game process...");
    }

    public void onFinish() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Sending to arena...");
        ItemStack item = new ItemStack(Material.WOOD_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        meta.setDisplayName(ChatColor.YELLOW + "Pig Killer");
        item.setItemMeta(meta);
        for(Player player : Bukkit.getOnlinePlayers()) {
            Location location = LocationUtil.getLocation("world", getInstance().getConfig().getString("arena"));
            player.teleport(location);
            player.getInventory().addItem(item);
        }
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
        return 10;
    }

}
