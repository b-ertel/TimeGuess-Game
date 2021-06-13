package at.timeguess.backend.ui.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;

/**
 * Controller for the game list view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class GameListController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private GameService gameService;

    private Collection<Game> filterGames;
    private Game selectedGame;
    private Boolean isAdmin = null;

    /**
     * Sets whether this instance is used for administrative access or not
     * (different lists are returned by {@link #getGames()}.
     * @param isAdmin true if yes, false if no
     */
    public void setAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Returns a list of all games.
     * @return list of games
     */
    public List<Game> getGames() {
        return isAdmin != null && isAdmin ? gameService.getAllGames() : gameService.getAllCurrent();
    }

    /**
     * Returns a list of all games for the given user (current and past).
     * @param  user user
     * @return list of games
     */
    public List<Game> getGames(User user) {
        return gameService.getByUser(user, false);
    }

    /**
     * Returns a list of all current games for the given user.
     * @param  user user
     * @return list of games
     */
    public List<Game> getGamesCurrent(User user) {
        return gameService.getByUser(user, true);
    }

    /**
     * Returns and sets a list of games, by default all returned by {@link #getGames()}
     * (helper methods for primefaces datatable filter and sort).
     * @return collection of games
     */
    public Collection<Game> getFilterGames() {
        if (filterGames == null) filterGames = getGames();
        return filterGames;
    }

    public void setFilterGames(Collection<Game> games) {
        filterGames = games;
    }

    /**
     * Returns and sets the currently selected game (helper methods for primefaces datatable contextmenu).
     * @return game
     */
    public Game getSelectedGame() {
        return selectedGame;
    }

    public void setSelectedGame(Game selectedGame) {
        this.selectedGame = selectedGame;
    }

    /**
     * Returns whether participation confirmation is possible for the given user and game.
     * @param user the user whose participation confirmation is checked for the given game.
     * @param game the game whose participation confirmation is checked for the given user.
     * @return true if participation confirmation is disabled, false otherwise.
     */
    public boolean isDisabledConfirmation(User user, Game game) {
        return gameService.disabledConfirmation(user, game);
    }

    /**
     * Checks if the given game can be deleted currently (it cannot while in states
     * {@link GameState#VALID_SETUP}, {@link GameState#PLAYED}, {@link GameState#HALTED}.
     * @param game game
     * @return true if it is, false if not
     */
    public boolean isLockedDelete(Game game) {
        if (game == null) return true;

        switch (game.getStatus()) {
            case VALID_SETUP:
            case PLAYED:
            case HALTED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Confirms given users participation in given game.
     * @param user user whose participation in given game is confirmed.
     * @param game game for which to confirm given users participation.
     */
    public void doConfirm(User user, Game game) {
        this.gameService.confirm(user, game);
    }
}
