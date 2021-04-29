package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

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
@Scope(WebApplicationContext.SCOPE_SESSION)
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
     * Sets the currently displayed game and reloads it form db. This game is targeted by any further calls of
     * {@link #doReloadGame()}, {@link #doSaveGame()} and {@link #doDeleteGame()}.
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
        doReloadGame();
    }

    /**
     * Returns the currently displayed game.
     * @return
     */
    public Game getGame() {
        return game;
    }

    /**
     * Returns a set containing all available game states.
     * @return
     */
    public Set<GameState> getAllGameStates() {
        return GameState.getGameStates();
    }

    /**
     * Returns a list of all teams.
     * @return
     */
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Returns a list of teams currently not playing.
     * @return
     */
    public List<Team> getAvailableTeams() {
        return teamService.getAvailableTeams();
    }

    /**
     * Return the
     * @return
     */
    public Set<GameState> getPossNextStates() {
        EnumSet<GameState> poss = EnumSet.noneOf(GameState.class);
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

    /**
     * Checks if state can change from the saved to the given.
     * @param next
     * @return
     */
    public boolean canTraverse(GameState next) {
        return getPossNextStates().contains(next);
    }

    /**
     * Checks if the given team is currently playing in any other than the saved game or not.
     * @param team
     * @return
     */
    public boolean isAvailableTeam(Team team) {
        return teamService.isAvailableTeamForGame(team, game);
    }

    /**
     * Checks if the maximum points can be changed for the saved game.
     * @return
     */
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

    /**
     * Checks if the topic can be changed for the saved game.
     * @return
     */
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

    /**
     * Checks if the associated teams can be changed for the saved game.
     * @return
     */
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

    /**
     * Checks if the saved game can be deleted currently (it should not while it is played).
     * @return
     */
    public boolean isLockedDelete() {
        switch (game.getStatus()) {
            case PLAYED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Confirms given users participation in current game.
     * @param user user whose participation in current game is confirmed.
     */
    public void doConfirm(User user) {
        this.gameService.confirm(user, game);
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
        if (doValidateGame()) {
            Game ret = this.gameService.saveGame(game);
            if (ret != null) game = ret;
        }
        else messageBean.alertErrorFailValidation("Saving game failed", "Input fields are invalid");
    }

    /**
     * Action to delete the currently displayed game.
     */
    public void doDeleteGame() {
        this.gameService.deleteGame(game);
        game = null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return true if all fields contain valid values, false otherwise
     */
    public boolean doValidateGame() {
        if (Strings.isBlank(game.getName())) return false;
        if (game.getMaxPoints() <= 0) return false;
        if (game.getTopic() == null) return false;
        if (game.getTeamCount() < 2) return false;
        return true;
    }
}
