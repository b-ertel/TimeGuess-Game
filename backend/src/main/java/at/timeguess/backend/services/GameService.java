package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating game data.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@CDIContextRelated
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;

    @CDIAutowired
    private WebSocketManager webSocketManager;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link PostConstruct} is not invoked, so autowiring is done manually
     */
    public GameService() {
        if (webSocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "webSocketManager");
        }
    }

    /**
     * Returns a list of all games.
     * @return
     */
    public List<Game> getAllGames() {
        return gameRepo.findAll();
    }

    /**
     * Returns a list of all games currently in setup state ({@link GameState#SETUP} or {@link GameState#VALID_SETUP}.
     * @return
     */
    public List<Game> getAllCurrent() {
        return gameRepo.findAllCurrent();
    }

    /**
     * Returns a list of all games with the given status.
     * @param gs
     * @return
     */
    public List<Game> getByStatus(GameState gs) {
        return gameRepo.findByStatus(gs);
    }

    /**
     * Returns a list of all games the given user is associated to, optionally restricted to games
     * currently in setup state ({@link GameState#SETUP} or {@link GameState#VALID_SETUP}.
     * @param user
     * @param current
     * @return
     */
    public List<Game> getByUser(User user, boolean current) {
        return current ? gameRepo.findByUserCurrent(user) : gameRepo.findByUserAll(user);
    }

    /**
     * Loads a single game identified by its id.
     * @param gameId
     * @return
     */
    public Game loadGame(Long gameId) {
        return gameRepo.findById(gameId).get();
    }

    /**
     * Saves the game. If the game is new the user requesting this operation will be stored as {@link Game#creator}.
     * Additionally fills gui message with success or failure info and triggers an update in the message channel.
     * @param game the game to save
     * @return the saved game
     * @apiNote Message handling ist done here, because this is the central place for saving games.
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('ADMIN')")
    public Game saveGame(Game game) {
        Game ret = null;
        try {
            boolean isNew = game.isNew();
            if (isNew) {
                game.setCreator(userService.getAuthenticatedUser());
            }
            ret = gameRepo.save(game);

            // fill ui message, send update and log
            messageBean.alertInformation(ret.getName(), isNew ? "New game created" : "Game updated");
            
            if (webSocketManager != null)
                webSocketManager.getUserRegistrationChannel().send(
                        Map.of("type", "gameUpdate", "name", game.getName(), "id", game.getId()));

            User auth = userService.getAuthenticatedUser();
            LOGGER.info("Game '{}' (id={}) was {} by User '{}' (id={})", ret.getName(), ret.getId(),
                    isNew ? "created" : "updated", auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            String msg = "Saving game failed";
            if (e.getMessage().contains("GAME(NAME)"))
                msg += String.format(": game named '%s' already exists", game.getName());
            messageBean.alertErrorFailValidation(game.getName(), msg);

            LOGGER.info("Saving game '{}' (id={}) failed, stack trace:", game.getName(), game.getId());
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Deletes the game.
     * Additionally fills gui message with success or failure info and triggers an update in the message channel.
     * @param game the game to delete
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('MANAGER') or hasAuthority('ADMIN')")
    public void deleteGame(Game game) {
        try {
            gameRepo.delete(game);

            // fill ui message, send update and log
            messageBean.alertInformation(game.getName(), "Game was deleted");

            if (webSocketManager != null)
                webSocketManager.getUserRegistrationChannel()
                    .send(Map.of("type", "gameUpdate", "name", game.getName(), "id", game.getId()));

            User auth = userService.getAuthenticatedUser();
            LOGGER.info("Game '{}' (id={}) was deleted by User '{}' (id={})", game.getName(), game.getId(),
                    auth.getUsername(), auth.getId());
        }
        catch (Exception e) {
            String name = game == null ? "Unknown" : game.getName();
            messageBean.alertErrorFailValidation(name, "Deleting game failed");
            LOGGER.info("Deleting game '{}' (id={}) failed, stack trace:", name, game == null ? "null" : game.getId());
            e.printStackTrace();
        }
    }

    /**
     * Confirms given users participation in given game.
     * @param user user whose participation in given game is confirmed.
     * @param game game for which to confirm given users participation.
     */
    public void confirm(User user, Game game) {
        if (!this.disabledConfirmation(user, game)) {
            game.getConfirmedUsers().add(user);
            this.saveGame(game);

            // show ui message and log
            messageBean.alertInformation(game.getName(), "Your participation was successfully confirmed");

            LOGGER.info("User '{}' (id={}) confirmed participation in game '{}' (id={})", user.getUsername(),
                    user.getId(), game.getName(), game.getId());
        }
    }

    /**
     * Returns whether participation confirmation is possible for the given user and game.
     * @param user the user whose participation confirmation is checked for the given game.
     * @param game the game whose participation confirmation is checked for the given user.
     * @return true if participation confirmation is disabled, false otherwise.
     */
    public boolean disabledConfirmation(User user, Game game) {
        // game confirmation generally disabled or user not invited?
        return disabledConfirmation(game) || !gameRepo.getIsPlayerInGame(user, game);
    }

    /**
     * Returns whether participation confirmation is possible for the given game.
     * @param game the game whose participation confirmation is checked.
     * @return true if participation confirmation is disabled, false otherwise.
     */
    public boolean disabledConfirmation(Game game) {
        switch (game.getStatus()) {
            case SETUP:
            case VALID_SETUP:
            case PLAYED:
                return false;
            default:
                return true;
        }
    }
}
