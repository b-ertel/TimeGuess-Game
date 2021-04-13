package at.timeguess.backend.services;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Service for accessing and manipulating game data.
 */
@Component
@Scope("application")
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private GameTeamService gameTeamService;

    @Autowired
    private MessageBean messageBean;

    public Collection<Game> getAllGames() {
        return gameRepo.findAll();
    }

    public Collection<Game> getAllCurrent() {
        return gameRepo.findAllCurrent();
    }

    public Collection<Game> getByStatus(GameState gs) {
        return gameRepo.findByStatus(gs);
    }

    public Collection<Game> getByUser(User user, boolean current) {
        return current ? gameRepo.findByUserCurrent(user) : gameRepo.findByUserAll(user);
    }

    public Game loadGame(Long gameId) {
        return gameRepo.findById(gameId).get();
    }

    /**
     * Saves the game.
     *
     * @param game the game to save
     * @return the updated game
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public Game saveGame(Game game) {
        boolean isNew = game.isNew();
        if (isNew)
            game.setCreator(userService.getAuthenticatedUser());

        // maybe a bit messy to replace gameteams this way
        Optional<Game> dbGame = gameRepo.findById(game.getId());
        if (!dbGame.isEmpty()) {
            Set<GameTeam> origTeams = dbGame.get().getGameTeams();
            origTeams.removeAll(game.getGameTeams());
            origTeams.stream().forEach(t -> gameTeamService.delete(t));
        }

        // save all the new gameteams
        game.getGameTeams().stream().forEach(t -> gameTeamService.save(t));

        Game ret = gameRepo.save(game);

        // faces message
        messageBean.alertInformation(isNew ? "New game created" : "Game updated", ret.getName());

        return ret;
    }

    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public void deleteGame(Game game) {
        gameRepo.delete(game);

        // faces message and audit log save
        messageBean.alertInformation("Game deleted", game.getName());

        LOGGER.info("Game '" + game.getName() + " was deleted!", game.getId());
    }

    /**
     * Confirms given users participation in given game.
     *
     * @param user user whose participation in given game is confirmed.
     * @param game game for which to confirm given users participation.
     */
    public void confirm(User user, Game game) {
        if (!this.disabledConfirmation(user, game)) {
            game.getConfirmedUsers().add(user);

            // return faces message for user
            messageBean.alertInformation("Your participation was successfully confirmed for game ", game.getName());

            // save log
            LOGGER.info("User {} confirmed participation in game {}", user.getUsername(), game.getName());
        }
    }

    /**
     * Returns whether participation confirmation is possible for the given user and
     * game.
     *
     * @param user the user whose participation confirmation is checked for the
     *             given game.
     * @param game the game whose participation confirmation is checked for the
     *             given user.
     * @return true if participation confirmation is disabled, false otherwise.
     */
    public boolean disabledConfirmation(User user, Game game) {
        // game confirmation generally disabled or user not invited?
        return disabledConfirmation(game) || gameRepo.findByUserConfirmation(user, game) < 1;
    }

    /**
     * Returns whether participation confirmation is possible for the given game.
     *
     * @param game the game whose participation confirmation is checked.
     * @return true if participation confirmation is disabled, false otherwise.
     */
    public boolean disabledConfirmation(Game game) {
        GameState status = game.getStatus();
        return !(status == GameState.SETUP || status == GameState.VALID_SETUP);
    }
}
