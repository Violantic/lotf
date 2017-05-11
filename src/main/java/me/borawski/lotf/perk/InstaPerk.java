/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.perk;

import me.borawski.lotf.GameHandler;
import me.borawski.lotf.Minigame;
import me.borawski.lotf.util.BarUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Ethan on 5/10/2017.
 */
public class InstaPerk implements Perk {

    @Override
    public String getName() {
        return "InstaKill";
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.SKULL_ITEM, 1);
    }

    @Override
    public void playerConsume(Player player) {
        GameHandler.instaKill.add(player.getUniqueId());
        new BukkitRunnable() {
            public void run() {
                GameHandler.instaKill.remove(player.getUniqueId());
                BarUtil.sendActionBar(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "YOUR INSTAKILL HAS RUN OUT!");
            }
        }.runTaskLater(Minigame.getInstance(), 20L*8L);
    }

}
