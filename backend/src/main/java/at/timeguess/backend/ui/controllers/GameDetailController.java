package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Cube;
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
    private CubeStatusController cubeStatusController;

    @Autowired
    private MessageBean messageBean;

    /**
     * Attribute to cache the currently displayed game
     */
    private Game game;
    private Cube orgCube;

    /**
     * Sets the currently displayed game and reloads it form db. This game is targeted by any
     * further calls of {@link #doReloadGame()}, {@link #doSaveGame()} and {@link #doDeleteGame()}.
     * @param game game
     */
    public void setGame(Game game) {
        this.game = game;
        doReloadGame();
    }

    /**
     * Returns the currently displayed game.
     * @return game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Returns a set containing all available game states.
     * @return set of game states
     */
    public Set<GameState> getAllGameStates() {
        return GameState.getGameStates();
    }

    /**
     * Returns a list of all teams.
     * @return list of teams
     */
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Returns a list of teams currently not playing.
     * @return list of teams
     */
    public List<Team> getAvailableTeams() {
        return teamService.getAvailableTeams();
    }

    /**
     * Return the possible state transitions from the current one.
     * @return set of game states
     */
    public Set<GameState> getPossNextStates() {
        EnumSet<GameState> poss = EnumSet.noneOf(GameState.class);
        // always include current state in list of options
        poss.add(game.getStatus());
        switch (game.getStatus()) {
            case SETUP:
                poss.add(GameState.CANCELED);
                break;
            case VALID_SETUP:
            case PLAYED:
                poss.add(GameState.HALTED);
                poss.add(GameState.CANCELED);
                break;
            case HALTED:
                poss.add(GameState.VALID_SETUP);
                poss.add(GameState.CANCELED);
                break;
            default:
                break;
        }
        return poss;
    }

    /**
     * Checks if state can change from the saved to the given.
     * @param  next next state
     * @return true if it can, false if not
     */
    public boolean canTraverse(GameState next) {
        return getPossNextStates().contains(next);
    }

    /**
     * Checks if the given team is currently playing in any other than the saved game or not.
     * @param  team team
     * @return true if it is, false if not
     */
    public boolean isAvailableTeam(Team team) {
        return teamService.isAvailableTeamForGame(team, game);
    }

    /**
     * Checks if the maximum points can be changed for the saved game.
     * @return true if it is, false if not
     */
    public boolean isLockedMaxPoints() {
        switch (game.getStatus()) {
            case VALID_SETUP:
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
     * @return true if it is, false if not
     */
    public boolean isLockedTopic() {
        switch (game.getStatus()) {
            case VALID_SETUP:
            case PLAYED:
            case FINISHED:
            case CANCELED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks if the associated teams can be changed for the saved game.
     * @return true if it is, false if not
     */
    public boolean isLockedTeam() {
        switch (game.getStatus()) {
            case VALID_SETUP:
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
     * Checks if the cube can be changed for the saved game.
     * @return true if it is, false if not
     */
    public boolean isLockedCube() {
        switch (game.getStatus()) {
            case VALID_SETUP:
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
        if (game != null) {
            game = gameService.loadGame(game.getId());
            orgCube = game.getCube();
        }
    }

    /**
     * Action to save the currently displayed game.
     */
    public void doSaveGame() {
        if (this.doValidateGame()) {
            this.checkCubeChange(orgCube, game.getCube());

            Game ret = this.gameService.saveGame(game);
            if (ret == null) {
                this.checkCubeChange(game.getCube(), orgCube);
            }
            else {
                game = ret;
                orgCube = game.getCube();
            }
        }
        else messageBean.alertErrorFailValidation("Saving game failed", "Input fields are invalid");
    }

    /**
     * Action to delete the currently displayed game.
     */
    public void doDeleteGame() {
        this.gameService.deleteGame(game);
        game = null;
        orgCube = null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return true if all fields contain valid values, false otherwise
     */
    public boolean doValidateGame() {
        if (Strings.isBlank(game.getName())) return false;
        if (game.getMaxPoints() <= 0) return false;
        if (game.getCube() == null || game.getCube().isNew()) return false;
        if (game.getTopic() == null || !game.getTopic().isEnabled()) return false;
        if (game.getTeamCount() < 2) return false;
        return true;
    }

    private void checkCubeChange(Cube fromCube, Cube toCube) {
        switch (game.getStatus()) {
            case SETUP:
                if (!Objects.equals(fromCube, toCube)) {
                    cubeStatusController.switchCube(fromCube, toCube);
                }
                break;

            case CANCELED:
                // once canceled any related cube is freed (no matter if saving the game succeeds)
                cubeStatusController.switchCube(fromCube, null);
                cubeStatusController.switchCube(toCube, null);
                break;

            default:
                break;
        }
    }
}
