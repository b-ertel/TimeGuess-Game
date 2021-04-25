package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Controller for the game detail view.
 */
@Component
public class GameDetailController implements Serializable {

    private static final long serialVersionUID = -3429788166384535247L;

    @Autowired
    private GameService gameService;

    @Autowired
    private TeamService teamService;
    @Autowired
    private MessageBean messageBean;

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
        if (doValidateGame()) game = this.gameService.saveGame(game);
        else messageBean.alertErrorFailValidation("Saving game failed", "Input fields are invalid");
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

    /**
     * Checks if all fields contain valid values.
     * @return true if all fields contain valid values, false otherwise
     */
    public boolean doValidateGame() {
        if (Strings.isBlank(game.getName())) return false;
        if (game.getMaxPoints() <= 0) return false;
        if (game.getTopic() == null) return false;
        return true;
    }

    /**
     * Confirms given users participation in current game.
     *
     * @param user user whose participation in current game is confirmed.
     */
    public void confirm(User user) {
        this.gameService.confirm(user, game);
    }

    public Set<Team> getPossibleTeams() {
        Set<Team> poss = new TreeSet<>(teamService.getAvailableTeams());
        // NOTE once availTeams is implemented correctly there is no more duplication
        // with actualTeams
        poss.addAll(game.getActualTeams());
        return poss;
    }

    public Set<GameState> getPossNextStates() {
        Set<GameState> poss = new TreeSet<>();
        // always include current state in list of options
        // always allow cancellation
        poss.add(game.getStatus());
        poss.add(GameState.CANCELED);
        switch (game.getStatus()) {
        case SETUP:
            poss.add(GameState.VALID_SETUP);
            break;
        case VALID_SETUP:
            poss.add(GameState.PLAYED);
            poss.add(GameState.HALTED);
            break;
        case PLAYED:
            poss.add(GameState.HALTED);
            poss.add(GameState.FINISHED);
            break;
        case HALTED:
            poss.add(GameState.PLAYED);
            break;
        default:
            break;
        }
        return poss;
    }

    public boolean canTraverse(GameState next) {
        return getPossNextStates().contains(next);
    }

    public boolean isLockedMaxPoints() {
        switch (game.getStatus()) {
        case PLAYED:
        case FINISHED:
        case CANCELED:
            return true;
        default:
            return false;
        }
    }

    public boolean isLockedTopic() {
        switch (game.getStatus()) {
        case PLAYED:
        case HALTED:
        case FINISHED:
        case CANCELED:
            return true;
        default:
            return false;
        }
    }

    public boolean isLockedTeam() {
        switch (game.getStatus()) {
        case PLAYED:
        case HALTED:
        case FINISHED:
        case CANCELED:
            return true;
        default:
            return false;
        }
    }

    public boolean isLockedDelete() {
        switch (game.getStatus()) {
        case PLAYED:
            return true;
        default:
            return false;
        }
    }
}
