/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

/**
 * Created by Ethan on 5/5/2017.
 */
public interface GameState {

    String getId();

    String getDescription();

    void onStart();

    void onFinish();

    boolean canMove();

    boolean canAlterTerrain();

    boolean canPvP();

    boolean canPvE();

    boolean canTalk();

    int getLength();

}
