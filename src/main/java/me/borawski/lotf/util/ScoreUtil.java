/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf.util;

import me.borawski.lotf.Minigame;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Ethan on 5/10/2017.
 */
public class ScoreUtil {

    public static int getPlace(UUID uuid) {
        Map<UUID, Integer> map = Minigame.getInstance().getHandler().getScore();
        Object[] a = map.entrySet().toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<UUID, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<UUID, Integer>) o1).getValue());
            }
        });

        List<UUID> sorted = new ArrayList<>();
        for (Object e : a) {
            sorted.add(((Map.Entry<UUID, Integer>) e).getKey());
        }

        return sorted.indexOf(uuid);
    }

    public static List<UUID> topThree() {
        List<Map.Entry<UUID, Integer>> sorted = entriesSortedByValues(Minigame.getInstance().getHandler().getScore());
        return new ArrayList<UUID>() {
            {
                for(int i = 0; i < 3; i++) {
                    try {
                        add(sorted.get(i).getKey());
                    } catch(Exception e) {

                    }
                }
            }
        };
    }

    static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

}
