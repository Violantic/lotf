/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.util;

import me.borawski.lotf.Minigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Ethan on 5/5/2017.
 */
public class LocationUtil {

    /**
     * Parses a location from config string
     *
     * @param world
     * @param path
     * @return
     */
    public static Location getLocation(String world, String path) {
        String[] strings = path.split(",");
        double x = Double.parseDouble(strings[0]);
        double y = Double.parseDouble(strings[1]);
        double z = Double.parseDouble(strings[2]);

        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static Location getPig(int id) {
        return getLocation("world", Minigame.getInstance().getConfig().getStringList("pigs").get(id));
    }
}
