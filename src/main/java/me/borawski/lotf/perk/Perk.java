/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.perk;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Ethan on 5/10/2017.
 */
public interface Perk {

    String getName();

    ItemStack getItem();

    void playerConsume(Player player);

}
