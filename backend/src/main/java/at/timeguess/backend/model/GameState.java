package at.timeguess.backend.model;

import java.util.EnumSet;

/**
 * Enumeration of different states a game can be in.
 */
public enum GameState {

    /**
     * Game is being created, settings are not valid yet
     */
    SETUP,
    /**
     * Game is ready to start as soon as enough teams are ready
     */
    VALID_SETUP,
    /**
     * Game is currently played: changing of most settings is blocked
     */
    PLAYED,
    /**
     * Game cannot continue until it is reconfigured, e.g. timeflip got lost, too many players disconnected, topic
     * cannot change
     */
    HALTED,
    /**
     * Game is done: changing settings is blocked
     */
    FINISHED,
    /**
     * Game was aborted: will not be used for statistics
     */
    CANCELED;

    private static EnumSet<GameState> gameStates = EnumSet.allOf(GameState.class);

    /**
     * Returns a set containing all available game states.
     * @return all possible game states
     */
    public static EnumSet<GameState> getGameStates() {
        return gameStates;
    }
}
