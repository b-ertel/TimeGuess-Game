package at.timeguess.backend.ui.controllers.game;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.services.GameService;

/**
 * Controller for the game detail view.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@Component
@Scope("view")
public class GameDetailController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3429788166384535247L;

    @Autowired
    private GameService gameService;

    /**
     * Attribute to cache the currently displayed game
     */
    private Game game;

    /**
     * Sets the currently displayed game and reloads it form db. This game is
     * targeted by any further calls of {@link #doReloadGame()},
     * {@link #doSaveGame()} and {@link #doDeleteGame()}.
     *
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
        doReloadGame();
    }

    /**
     * Returns the currently displayed game.
     *
     * @return
     */
    public Game getGame() {
        return game;
    }

    /**
     * Action to force a reload of the currently displayed game.
     */
    public void doReloadGame() {
        game = gameService.loadGame(game.getId());
    }

    /**
     * Action to save the currently displayed game.
     */
    public void doSaveGame() {
        game = this.gameService.saveGame(game);
    }

    /**
     * Action to delete the currently displayed game.
     */
    public void doDeleteGame() {
        if (game != null) {
            this.gameService.deleteGame(game);
            game = null;
        }
    }

}
