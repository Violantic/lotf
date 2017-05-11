/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.state;

import me.borawski.lotf.GameState;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.util.BarUtil;
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

    /**
     * Instance
     */
    private Minigame instance;

    /**
     * const
     * @param instance
     */
    public LobbyState(Minigame instance) {
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
        return "lobby";
    }

    public String getDescription() {
        return "Wait for the game to start";
    }

    /**
     * Notify that the lobby-ready stage is going to start
     */
    public void onStart() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Starting game process...");
        for(Player player : Bukkit.getOnlinePlayers()) {
            BarUtil.sendActionBar(player, ChatColor.YELLOW + "Starting game process...");
        }
    }

    /**
     * Go ahead and give players equipment, and teleport to slaughter house.
     */
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
            BarUtil.sendActionBar(player, ChatColor.YELLOW + "Sending to arena...");
        }
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
     * 20 Seconds
     * @return
     */
    public int getLength() {
        return 20;
    }

}
