/*
 * Copyright (c) 2017. This code was written by Ethan Borawski, any use without permission will result in a court action. Check out my GitHub @ https://github.com/Violantic
 */

package me.borawski.lotf;

/**
 * Created by Ethan on 5/5/2017.
 */
public interface GameState {

    /**
     * The String id that the game checks sequence logic with.
     * @return
     */
    String getId();

    /**
     * Description... Enough said :P
     * @return
     */
    String getDescription();

    /**
     * Method that invokes upon start of new game state
     */
    void onStart();

    /**
     * Method that invokes upon finish of game state
     */
    void onFinish();

    // ------------------------------- // GameState Rules // ------------------------------- //
    //                  *** TO CHANGE VALUES, SIMPLY OVERRIDE METHOD ***                     //
    // ------------------------------------------------------------------------------------- //

    /**
     * Determines if players can move from their current location
     * to a new location.
     * @return
     */
    boolean canMove();

    /**
     * Determines if players can edit/remove/interact the blocks that they encounter.
     * @return
     */
    boolean canAlterTerrain();

    /**
     * Determines if players can attack each other.
     * @return
     */
    boolean canPvP();

    /**
     * Determines if players can attack the mobs :P
     * @return
     */
    boolean canPvE();

    /**
     * Determines whether or not players can publicly chat.
     * @return
     */
    boolean canTalk();

    /**
     * The length of this sequence interval
     * @return
     */
    int getLength();

}
