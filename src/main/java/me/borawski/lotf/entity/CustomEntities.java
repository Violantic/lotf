/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.entity;

import net.minecraft.server.v1_11_R1.Entity;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import net.minecraft.server.v1_11_R1.RegistryMaterials;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.borawski.lotf.entity.EvilPig.getPrivateField;

/**
 * Created by Ethan on 5/8/2017.
 */
public enum CustomEntities {

    /**
     * Custom Entities
     */
    CUSTOM_EVIL_PIG("EvilPig", 90, EvilPig.class);

    /**
     * cons
     * @param name
     * @param id
     * @param custom
     */
    private CustomEntities(String name, int id, Class<? extends Entity> custom) {
        registerEntity(name, id, custom);
    }

    /**
     * Spawn the entity
     * @param entity
     * @param loc
     */
    public static void spawnEntity(Entity entity, Location loc) {
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        ((CraftWorld)loc.getWorld()).getHandle().addEntity(entity);
    }

    /**
     * Register the entity, done upon initialization of class
     * @param name
     * @param id
     * @param customClass
     */
    public void registerEntity(String name, int id, Class customClass) {
        MinecraftKey key = new MinecraftKey(name);
        try {
            ((RegistryMaterials) getPrivateStatic(EntityTypes.class, "b")).a(id, key, customClass);
            ((Set) getPrivateStatic(EntityTypes.class, "d")).add(key);
            ((List) getPrivateStatic(EntityTypes.class, "g")).set(id, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reflection for getting a Field/Object from a class, and making it accessible (static)
     * @param clazz
     * @param f
     * @return
     * @throws Exception
     */
    private static Object getPrivateStatic(Class clazz, String f) throws Exception {
        Field field = clazz.getDeclaredField(f);
        field.setAccessible(true);
        return field.get(null);
    }

}
