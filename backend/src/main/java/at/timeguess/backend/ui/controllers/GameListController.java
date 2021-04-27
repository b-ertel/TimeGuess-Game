package at.timeguess.backend.ui.controllers;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;

/**
 * Controller for the game list view.
 */
@Component
@Scope("view")
public class GameListController {

    @Autowired
    private GameService gameService;

    private Collection<Game> filterGames;
    private Game selectedGame;
    private Boolean isAdmin = null;

    /**
     * Sets whether this instance is used for administrative access or not
     * (different lists are returned by {@link getGames()}.
     * @param isAdmin
     */
    public void setAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Returns a list of all games.
     */
    public Collection<Game> getGames() {
        return isAdmin != null && isAdmin ? gameService.getAllGames() : gameService.getAllCurrent();
    }

    /**
     * Returns a list of all games for the given user (current and past).
     */
    public Collection<Game> getGames(User user) {
        return gameService.getByUser(user, false);
    }

    /**
     * Returns a list of all current games for the given user.
     */
    public Collection<Game> getGamesCurrent(User user) {
        // TODO: use this for production (IMPORTANT!):
        // gameService.getByUser(user, true);
        return gameService.getAllGames();
    }

    /**
     * Returns and sets a list of games, by default all returned by {@link getGames()}
     * (helper methods for primefaces datatable filter and sort).
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
     * Confirms given users participation in given game.
     * @param user user whose participation in given game is confirmed.
     * @param game game for which to confirm given users participation.
     */
    public void doConfirm(User user, Game game) {
        this.gameService.confirm(user, game);
    }
}
