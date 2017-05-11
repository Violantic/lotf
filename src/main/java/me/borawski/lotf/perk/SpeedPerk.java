/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.perk;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Ethan on 5/10/2017.
 */
public class SpeedPerk implements Perk {

    @Override
    public String getName() {
        return "Speed";
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.FEATHER, 1);
    }

    @Override
    public void playerConsume(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*20, 2));
    }
}
