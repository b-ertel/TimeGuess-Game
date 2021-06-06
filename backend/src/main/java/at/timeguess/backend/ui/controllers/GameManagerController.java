package at.timeguess.backend.ui.controllers;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.events.ChannelPresenceEvent;
import at.timeguess.backend.events.ChannelPresenceEventListener;
import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * This controller is responsible for showing a term in the game window with websockets
 * and managing all currently running games.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class GameManagerController {

    /**
     * Possible states for a current round in any running game.
     */
    public enum RoundState {
        /**
         * No round is active.
         */
        NONE,
        /**
         * Round is set up and waiting for cube infos.
         */
        STARTING,
        /**
         * Countdown is running.
         */
        RUNNING,
        /**
         * Round ended by cube or countdown and waiting for validation.
         */
        VALIDATING,
        /**
         * Round state is inconsistent and must be reset.
         */
        ERROR
    }

    /**
     * Possible reasons for a current game to wait for some state change.
     */
    public enum WaitReason {
        /**
         * There is no reason to wait.
         */
        NONE,
        /**
         * Not all set teams are present, per team at least one player must be in the gameroom.
         */
        TEAMS_ABSENT,
        /**
         * The cube state is reported to be offline by {@link CubeStatusController}.
         */
        CUBE_OFFLINE,
        /**
         * The game was halted from outside (by some user with privileges to do so).
         */
        GAME_HALTED
    }

    /**
     * Possible reasons for no current game available.
     */
    public enum NoGameReason {
        /**
         * There is a game currently available.
         */
        NONE,
        /**
         * The game has finished by reaching the set maximum points or by running out of terms.
         */
        GAME_FINISHED,
        /**
         * The game was canceled from outside (by some user with privileges to do so).
         */
        GAME_CANCELED,
        /**
         * The game is in some other state not ready to be played.
         */
        GAME_WRONGSTATE,
        /**
         * The game is unknown.
         */
        GAME_UNKNOWN
    }

    @Autowired
    private GameService gameService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameLogicService gameLogic;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
    private ChannelPresenceEventListener channelPresenceEventListener;

    // cannot implement two different Consumers!
    private Consumer<ConfiguredFacetsEvent> consumerConfiguredFacetsEvent =
        (cfEvent) -> GameManagerController.this.onConfiguredFacetsEvent(cfEvent);
    private Consumer<ChannelPresenceEvent> consumerChannelPresenceEvent =
        (cpEvent) -> GameManagerController.this.onChannelPresenceEvent(cpEvent);

    // contains all current games with states
    private Status status = new Status();

    @PostConstruct
    public void setup() {
        configuredfacetsEventListener.subscribe(consumerConfiguredFacetsEvent);
        channelPresenceEventListener.subscribe("newRoundChannel", consumerChannelPresenceEvent);
    }

    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(consumerConfiguredFacetsEvent);
        channelPresenceEventListener.unsubscribe("newRoundChannel", consumerChannelPresenceEvent);
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     * Method is called on facet-event, checks to which game the event belongs and estimates whether a round should start or end.
     */
    public synchronized void onConfiguredFacetsEvent(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (status.containsCube(configuredFacetsEvent.getCube())) {
            GameData data = status.getGameDataReloaded(configuredFacetsEvent.getCube());

            // only act if game is currently played
            if (data != null && data.game.getStatus() == GameState.PLAYED && !data.isValidating) {
                data.isRoundActive = !data.isRoundActive;

                if (data.isRoundActive) {
                    startRoundByCube(data, configuredFacetsEvent.getCubeFace());
                }
                else {
                    data.isValidating = true;
                    endRoundByCube(data, configuredFacetsEvent.getCubeFace());
                }
            }
        }
    }

    /**
     * A method for processing a {@link ChannelPresenceEvent}.
     * Checks for each game currently to be played whether enough players are present and sets the games state accordingly.
     * Is called by a channel supervisor when players enter or leave the game room.
     */
    public synchronized void onChannelPresenceEvent(ChannelPresenceEvent channelPresenceEvent) {
        if (!channelPresenceEvent.getChannel().equals("newRoundChannel")) return;

        final Set<Long> userIds = channelPresenceEvent.getUserIds();
        status.reloaded().forEach(data -> {
            // count present teams
            data.updateTeamPresence(userIds);

            // switch state if appropriate and act according to change
            switchGameState(data);
        });
    }

    /**
     * Method to find the game in which a given user is currently playing, called by{@link CountDownController}
     * @param  user to find current game
     * @return current game
     */
    public Game getCurrentGameForUser(User user) {
        Optional<GameData> data = status.getGameDataForUser(user);
        return data.isPresent() ? data.get().game : null;
    }

    /**
     * Method to get the current round for a given user, called by {@link GameRoundController}
     * @param  user to find current round
     * @return current round
     */
    public Round getCurrentRoundForUser(User user) {
        Optional<GameData> data = status.getGameDataForUser(user);
        return data.isPresent() ? data.get().currentRound : new Round();
    }

    /**
     * Checks if the given cube is currently in use.
     * @param  cube to find game for
     * @return
     */
    public boolean hasCurrentGameForCube(Cube cube) {
        return status.containsCube(cube);
    }

    /**
     * Returns the current round state for the given game.
     * @param  game
     * @return
     */
    public RoundState getRoundStateForGame(Game game) {
        GameData data = status.getGameData(game);

        if (data == null || data.currentRound == null) return RoundState.NONE;
        if (!data.isRoundActive && !data.isValidating) return RoundState.STARTING;
        if (data.isRoundActive && !data.isValidating) return RoundState.RUNNING;
        if (!data.isRoundActive && data.isValidating) return RoundState.VALIDATING;

        return RoundState.ERROR;
    }

    /**
     * Returns the current reason to wait for the given game to continue.
     * @param  game
     * @return
     */
    public WaitReason getWaitReasonForGame(Game game) {
        GameData data = status.getGameData(game);

        if (data != null && data.game != null) {
            switch (data.game.getStatus()) {
                case HALTED:
                    return WaitReason.GAME_HALTED;

                case VALID_SETUP:
                    if (!data.isCubeOnline) return WaitReason.CUBE_OFFLINE;
                    if (!data.isTeamsPresent) return WaitReason.TEAMS_ABSENT;
                    break;

                default:
                    break;
            }
        }
        return WaitReason.NONE;
    }

    /**
     * Returns the current reason to wait for the given game to continue.
     * @param  game
     * @return
     */
    public NoGameReason getNoGameReasonForGame(Game game) {
        GameData data = status.getGameData(game);

        if (data == null || data.game == null) return NoGameReason.GAME_UNKNOWN;

        switch (data.game.getStatus()) {
            case FINISHED:
                return NoGameReason.GAME_FINISHED;
            case CANCELED:
                return NoGameReason.GAME_CANCELED;
            default:
                return NoGameReason.GAME_WRONGSTATE;
        }
    }

    /**
     * Method to add a newly created game to the saved list of currently available games
     * and send notifications to all participating team members.
     * @param game
     */
    public void addGame(Game game) {
        // add game to internal list and change game status
        if (status.addGame(game)) {

            // send invitation to all contained team members
            websocketManager.getMessageChannel().send(
                Map.of("type", "gameInvitation", "name", game.getName(), "id", game.getId()),
                getAllUserIdsOfGameTeams(game));
        }
    }

    /**
     * Method to switch from active to validating round, called by {@link CountDownController}
     * @param user user to find current round for
     */
    public void endRoundByCountDown(User user) {
        // put the round inactive to enable starting a new round via flip
        endRoundByCountDown(status.getGameDataForUser(user).orElse(null));
    }

    /**
     * Method to validate and save a round of a game.
     * Checks if a team reached the maximum of points or if all enabled terms of topic have been used.
     * Send corresponding message to all users in the game
     * @param game
     * @param v validation of round
     */
    public void validateRoundOfGame(Game game, Validation v) {
        GameData data = status.getGameDataReloaded(game);

        // only act if game is currently played
        if (data != null && data.game.getStatus() == GameState.PLAYED && data.isValidating) {
            gameLogic.saveLastRound(data.game, v);
            data.isValidating = false;

            if (gameLogic.teamReachedMaxPoints(data.game, data.currentRound.getGuessingTeam())) {
                endGameFinished(data);
            }
            else if (gameLogic.stillTermsAvailable(data.game)) {
                endRoundValidated(data);
            }
            else {
                endGameTermsOver(data);
            }
        }
    }

    /**
     * called by {@link CubeStatusController} if Cube is offline -> GameStatus should be put to {@link GameState#VALID_SETUP}
     * @param cube which is offline
     */
    public void cubeOffline(Cube cube) {
        switchGameState(status.setCubeState(cube, false));
    }

    /**
     * called by {@link CubeStatusController} if Cube is online -> GameStatus should be put to PLAYED if otherwise appropriate
     * @param cube which is online
     */
    public void cubeOnline(Cube cube) {
        switchGameState(status.setCubeState(cube, true));
    }

    /**
     * called by {@link CubeStatusController} if there is a health status to report i.e. low battery,
     * no connection puts current game on halted if cube is OFFLINE
     * @param cube to report to the current game
     */
    public void healthNotification(Cube cube) {
        sendHealthNotification(status.getGameData(cube));
    }

    /**
     * Method to put all usernames of players, that are online, into a list
     * @param  listOfTeams
     * @return list of user ids
     */
    private Set<Long> getAllUserIdsOfGameTeams(Game game) {
        return game.getTeams().stream()
            .flatMap(t -> t.getTeamMembers().stream().map(User::getId))
            .collect(Collectors.toSet());
    }

    /**
     * Switches the state of the given game and acts according to a change if necessary.
     * @param data
     */
    private void switchGameState(GameData data) {
        if (data == null) return;

        GameState oldState = data.game.getStatus();

        switch (status.switchGameState(data)) {
            case PLAYED:
                switch (oldState) {
                    case PLAYED:
                        setupGame(data);
                        break;

                    default:
                        if (data.currentRound == null) {
                            startGame(data);
                        }
                        else {
                            restartGame(data);
                        }
                        break;
                }
                break;

            case CANCELED:
                endGameCanceled(data);
                break;

            default:
                pauseGame(data);
                break;
        }
    }

    /* --- start - push message sending functions --- */
    private void setupGame(GameData data) {
        sendGameMessage(data.game, data.isRoundActive || data.isValidating ? "setup" : "setupandupdate");
    }

    /**
     * Method to get informations of next round, before cube starts the round
     * @param data data of game for which informations of next round should be estimated
     */
    private void startGame(GameData data) {
        data.currentRound = gameLogic.getNextRound(data.game);
        sendGameMessage(data.game, "startGame");
    }

    private void restartGame(GameData data) {
        sendGameMessage(data.game, "restartGame");
    }

    private void pauseGame(GameData data) {
        sendGameMessage(data.game, "pauseGame");
    }

    private void endGameCanceled(GameData data) {
        status.endGame(data, false);
        sendGameMessage(data.game, "gameCanceled");
    }

    private void endGameFinished(GameData data) {
        status.endGame(data, true);
        sendGameMessage(data.game, "gameOver");
    }

    private void endGameTermsOver(GameData data) {
        Team winningTeam = gameLogic.getTeamWithMostPoints(data.game);
        data.game.setMaxPoints(roundService.getPointsOfTeamInGame(data.game, winningTeam));

        status.endGame(data, true);
        sendGameMessage(data.game, "termsOver");
    }

    /**
     * Method that starts a new Round by the cube for a game.
     * It initializes a new round and also calls a method to start the countdown.
     * @param data     data of game for which round should be started
     * @param cubeFace face that sets round parameters
     */
    private void startRoundByCube(GameData data, CubeFace cubeFace) {
        data.currentRound = gameLogic.getCubeInfosIntoRound(data.currentRound, cubeFace);
        sendGameMessage(data.game, "startRound");
    }

    private void endRoundByCube(GameData data, CubeFace cubeFace) {
        sendGameMessage(data.game, "endRoundViaFlip");
    }

    private void endRoundByCountDown(GameData data) {
        // send countdown ended only on first call, but avoid inconsistency in properties
        if (data != null && (!data.isValidating || data.isRoundActive)) {
            data.isValidating = true;
            data.isRoundActive = false;
            sendGameMessage(data.game, "endRoundViaCountDown");
        }
    }

    /**
     * Method to get informations of next round, before cube starts the round
     * @param data data of game for which informations of next round should be estimated
     */
    private void endRoundValidated(GameData data) {
        data.currentRound = gameLogic.getNextRound(data.game);
        sendGameMessage(data.game, "validatedRound");
    }

    public void sendHealthNotification(GameData data) {
        if (data != null) sendGameMessage(data.game, "healthMessage");
    }

    private void sendGameMessage(Game game, String message) {
        if (game != null) { this.websocketManager.getNewRoundChannel().send(message, getAllUserIdsOfGameTeams(game)); }
    }
    /* --- end - push message sending functions --- */

    private class Status implements Iterable<GameData> {

        private Map<Cube, GameData> cube2Game = new ConcurrentHashMap<>();

        private static final String DEFAULT_USER = "system";
        private static final String DEFAULT_PASSWORD = "passwd";

        public boolean containsCube(Cube cube) {
            return cube2Game.containsKey(cube);
        }

        public GameData getGameData(Cube cube) {
            return getGameData(cube, false);
        }

        public GameData getGameData(Game game) {
            return getGameData(game, false);
        }

        public GameData getGameDataReloaded(Cube cube) {
            return getGameData(cube, true);
        }

        public GameData getGameDataReloaded(Game game) {
            return getGameData(game, true);
        }

        public Optional<GameData> getGameDataForUser(User user) {
            return cube2Game.values().stream()
                .filter(gd -> gd.game.getTeams().stream().anyMatch(t -> t.getTeamMembers().contains(user)))
                .findFirst();
        }

        public GameData setCubeState(Cube cube, boolean isOnline) {
            GameData data = getGameData(cube);
            if (data != null) data.isCubeOnline = isOnline;
            return data;
        }

        /**
         * Switches the given games state if appropriate and saves the game.
         * @return game status after possibly changing it and saving the game
         */
        public GameState switchGameState(GameData data) {
            GameState ret = data.game.getStatus();
            switch (ret) {
                case VALID_SETUP:
                case PLAYED:
                    // GameState newState = data.isTeamsPresent && data.isCubeOnline ?
                    GameState newState = data.isTeamsPresent ? GameState.PLAYED : GameState.VALID_SETUP;
                    Game game = setGameState(data.game, newState);
                    // problematic: if (game != null) data.game = game;
                    assert newState == game.getStatus();
                    return newState;

                default:
                    return ret;
            }
        }

        /**
         * Adds the given game to the inner list, sets its state to {@link GameState#VALID_SETUP} and saves it.
         * @param  game
         * @throws IllegalArgumentException if game is null
         * @throws IllegalArgumentException if game is not in state {@link GameState#SETUP}
         * @throws IllegalArgumentException if set cube is already associated with another contained game
         */
        @SuppressWarnings("unlikely-arg-type")
        public boolean addGame(Game game) {
            if (game == null)
                throw new IllegalArgumentException("Game must not be null");

            // check correct initial state
            if (game.getStatus() != GameState.SETUP)
                throw new IllegalArgumentException(String.format(
                    "Game %s is in state %s, must be in %s state for adding", game, game.getStatus(), GameState.SETUP));

            // check cube already in use
            Cube cube = game.getCube();
            if (cube2Game.containsKey(cube) && !cube2Game.equals(game))
                throw new IllegalArgumentException(
                    String.format("Cube %s is already in use for game %s", cube, cube2Game.get(cube)));

            // save new gamestate, on error revert state
            Game ret = setGameState(game, GameState.VALID_SETUP);
            if (ret == null) return false;

            // put into internal list tied to cube
            cube2Game.put(cube, new GameData(game));
            return true;
        }

        /**
         * Ends the game. Sets its status to finished, saves it in the database
         * and removes it from the inner list.
         * @param game game that finishes
         */
        public void endGame(GameData data, boolean finished) {
            setGameState(data.game, finished ? GameState.FINISHED : GameState.CANCELED);
            cube2Game.remove(data.game.getCube());
        }

        @Override
        public Iterator<GameData> iterator() {
            return cube2Game.values().iterator();
        }

        public Stream<GameData> reloaded() {
            return cube2Game.values().stream().map(gd -> reload(gd));
        }

        private GameData getGameData(Cube cube, boolean reload) {
            GameData data = cube2Game.get(cube);
            if (reload) data = reload(data);
            return data;
        }

        private GameData getGameData(Game game, boolean reload) {
            return game == null ? null : getGameData(game.getCube(), reload);
        }

        /**
         * Reloads the given instances game from db and return the input value for convenience.
         * @param  data instance to reload
         * @return given instance reloaded
         */
        private GameData reload(GameData data) {
            if (data == null) return null;

            // ensure authenticated (is not the case when called from presence event)
            setAuthenticatedUserDefault();

            // transfer relevant (possibly changed outside) values from fresh instance
            // to saved one, the other way around rounds cannot be saved
            // => hibernate exception 'detached entity passed to persist'
            Game game = gameService.loadGame(data.game.getId());
            if (game != null) {
                data.game.setName(game.getName());
                data.game.setMaxPoints(game.getMaxPoints());
                data.game.setTopic(game.getTopic());
                data.game.setStatus(game.getStatus());
            }
            return data;
        }

        /**
         * Tries to save given game with given state, on error resets old state in instance.
         * @param  game  game to save with given state
         * @param  state state to save in given game
         * @return       saved game or null if given game could not be saved,
         *               given game if state is already set
         */
        private Game setGameState(Game game, GameState newState) {
            GameState oldState = game.getStatus();
            if (oldState != newState) {
                setAuthenticatedUserDefault();
                game.setStatus(newState);
                Game ret = gameService.saveGame(game);
                if (ret == null) game.setStatus(oldState);
                return ret;
            }
            return game;
        }

        private void setAuthenticatedUserDefault() {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication auth = context.getAuthentication();
            if (auth == null) {
                auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(DEFAULT_USER, DEFAULT_PASSWORD));
                context.setAuthentication(auth);
            }
        }
    }

    public static class GameData {

        final Game game;
        Round currentRound = null;
        boolean isRoundActive = false;
        boolean isValidating = false;
        boolean isTeamsPresent = false;
        // must assume that cube is online in the first place
        // because online-message is not sent often enough to start with false (game would have to wait for it...)
        boolean isCubeOnline = true;

        public GameData(Game game) {
            if (game == null) throw new IllegalArgumentException("Cannot hold null game");
            this.game = game;
        }

        /**
         * Calculates the number of present teams from the given list of user ids
         * and sets its internal field accordingly.
         * @param userIds
         */
        public void updateTeamPresence(Set<Long> userIds) {
            // count present teams (one player per team must be present)
            Set<Team> teams = game.getTeams();
            long count = teams.stream().filter(t -> t.getTeamMembers().stream()
                .anyMatch(u -> userIds.contains(u.getId()))).count();

            isTeamsPresent = count >= game.getTeamCount();
        }

        @Override
        public int hashCode() {
            final int prime = 11;
            int result = 57;
            result = prime * result + (game == null ? 0 : game.hashCode());
            return result;
        }

        /**
         * Checks for equality with itself or the held game.
         * @return true if obj is or holds the same game, false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;

            if (obj == null)
                return false;

            if (obj.getClass() == Game.class) { final Game other = (Game) obj; return Objects.equals(game, other); }

            if (obj.getClass() == GameData.class) {
                final GameData other = (GameData) obj;
                return Objects.equals(game, other.game);
            }

            return false;
        }

        @Override
        public String toString() {
            return String.format("GameData<%s>", game);
        }
    }
}
