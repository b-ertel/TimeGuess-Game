package at.timeguess.backend.model.game;

public enum GameState {
    SETUP, // just created, contains invalid settings
    VALID_SETUP, // ready to start (game starts when all teams are ready)
    PLAYED, // during active game play (changing most settings is blocked)
    HALTED, // game play can not continue until reconfigured (thing timeflip getting lost,
            // too many players disconnect, topic can not change)
    FINISHED // game is done (changing settings is blocked)
}
