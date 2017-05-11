/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.entity;

import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ethan on 5/8/2017.
 */
public class EvilPig extends EntityPig {

    /**
     * Constructor.
     * Gives custom pathfinder goals to make pigs
     *      1.) Super fast
     *      2.) Panic more easily
     *      3.) Want to randomly stroll more
     * @param world
     */
    public EvilPig(org.bukkit.World world) {
        super(((CraftWorld)world).getHandle());
        Set goalB = (Set)getPrivateField("b", PathfinderGoalSelector.class, goalSelector); goalB.clear();
        Set goalC = (Set)getPrivateField("c", PathfinderGoalSelector.class, goalSelector); goalC.clear();
        Set targetB = (Set)getPrivateField("b", PathfinderGoalSelector.class, targetSelector); targetB.clear();
        Set targetC = (Set)getPrivateField("c", PathfinderGoalSelector.class, targetSelector); targetC.clear();

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 3.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    /**
     * Reflection to get private pathfinder goals/targets from the pathfinder class
     * @param fieldName
     * @param clazz
     * @param object
     * @return
     */
    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch(NoSuchFieldException e) {
            e.printStackTrace();
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

}
