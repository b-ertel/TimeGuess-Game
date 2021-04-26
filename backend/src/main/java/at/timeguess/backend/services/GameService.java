package at.timeguess.backend.services;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@ApplicationScoped
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
     * @return the saved game
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public Game saveGame(Game game) {
        boolean isNew = game.isNew();
        if (isNew) {
            game.setCreator(userService.getAuthenticatedUser());
        } else {
            // maybe a bit messy to replace gameteams this way
            Optional<Game> dbGame = gameRepo.findById(game.getId());
            if (!dbGame.isEmpty()) {
                Set<GameTeam> origTeams = dbGame.get().getTeams();
                origTeams.removeAll(game.getTeams());
                origTeams.stream().forEach(t -> gameTeamService.delete(t));
            }
        }

        // save all the new gameteams
        game.getTeams().stream().forEach(t -> gameTeamService.save(t));

        Game ret = gameRepo.save(game);

        // show ui message and log
        messageBean.alertInformation(ret.getName(), isNew ? "New game created" : "Game updated");

        User auth = userService.getAuthenticatedUser();
        LOGGER.info("Game '{}' (id={}) was {} by User '{}' (id={})", ret.getName(), ret.getId(),
                isNew ? "created" : "updated", auth.getUsername(), auth.getId());

        return ret;
    }

    /**
     * Deletes the game.
     * @param game the game to delete
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public void deleteGame(Game game) {
        try {
            gameRepo.delete(game);
    
            // show ui message and log
            messageBean.alertInformation(game.getName(), "Game was deleted");
    
            User auth = userService.getAuthenticatedUser();
            LOGGER.info("Game '{}' (id={}) was deleted by User '{}' (id={})", game.getName(), game.getId(),
                    auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            messageBean.alertError(game.getName(), "Deleting game failed");
            LOGGER.info("Deleting game '{}' (id={}) failed, stack trace:", game.getName(), game.getId());
            e.printStackTrace();
        }
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

            // show ui message and log
            messageBean.alertInformation(game.getName(), "Your participation was successfully confirmed");

            LOGGER.info("User '{}' (id={}) confirmed participation in game '{}' (id={})", user.getUsername(),
                    user.getId(), game.getName(), game.getId());
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
