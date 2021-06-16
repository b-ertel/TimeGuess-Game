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
import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
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
import at.timeguess.backend.services.GameTeamService;
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
    private GameTeamService gameTeamService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private GameLogicService gameLogicService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
    private ChannelPresenceEventListener channelPresenceEventListener;

    @Value("${jobs.enabled:true}")
    private boolean isJobEnabled;

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
     * Scheduled task which checks in a fixed interval for game states changed from outside.
     * Checked states are {@link GameState#SETUP}, {@link GameState#HALTED} and {@link GameState#CANCELED}.
     */
    @Scheduled(fixedDelayString = "${backend.game.check.delay.seconds:5}000")   // 5000 means every 5 seconds
    public void updateGameStatus() {
        if (isJobEnabled) {
            for (Game game : gameService.getByStatus(new GameState[] {
                GameState.SETUP, GameState.VALID_SETUP, GameState.HALTED, GameState.CANCELED })) {
                switch (game.getStatus()) {
                    case SETUP:
                        addGame(game);
                        break;

                    case VALID_SETUP:
                        reviveGame(game);
                        break;

                    case CANCELED:
                        removeGame(game);
                        break;

                    case HALTED:
                        haltGame(game);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     * Method is called on facet-event, checks to which game the event belongs and estimates whether a round should start or end.
     * When the countdown is running and the gamestate changes to paused, the current round can be invalidated by throwing the cube.
     * That way the same guessing team gets a new term when the game restarts.
     * @param configuredFacetsEvent event
     */
    public synchronized void onConfiguredFacetsEvent(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (status.containsCube(configuredFacetsEvent.getCube())) {
            GameData data = status.getGameDataReloaded(configuredFacetsEvent.getCube());

            switch (data.game.getStatus()) {
                case PLAYED:
                    // start countdown and validation only when game is currently running
                    switch (getRoundState(data)) {
                        case STARTING:
                            startRoundByCube(data, configuredFacetsEvent.getCubeFace());
                            break;

                        case RUNNING:
                            endRoundByCube(data, configuredFacetsEvent.getCubeFace());
                            break;

                        default:
                            break;
                    }
                    break;

                case HALTED:
                case VALID_SETUP:
                    // set round to invalid to repeat it with different term
                    switchRoundState(data, RoundState.ERROR);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * A method for processing a {@link ChannelPresenceEvent}.
     * Checks for each game currently to be played whether enough players are present and sets the games state accordingly.
     * Is called by a channel supervisor when players enter or leave the game room.
     * @param channelPresenceEvent event
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
     * Method to find the game in which a given user is currently playing, called by {@link GameRoundController}.
     * @param  user to find current game
     * @return current game
     */
    public Game getCurrentGameForUser(User user) {
        Optional<GameData> data = status.getGameDataForUser(user);
        return data.isPresent() ? data.get().game : null;
    }

    /**
     * Method to get the current round for a given game, called by {@link GameRoundController}.
     * @param  game to find current round
     * @return current round
     */
    public Round getCurrentRoundForGame(Game game) {
        GameData data = status.getGameData(game);
        return data == null ? new Round() : data.currentRound;
    }

    /**
     * Checks if the given cube is currently in use, called by {@link CubeStatusController}.
     * @param  cube to find game for
     * @return true if it has, false if not
     */
    public boolean hasCurrentGameForCube(Cube cube) {
        return status.containsCube(cube);
    }

    /**
     * Returns the current round state for the given game.
     * @param  game game
     * @return round state
     */
    public RoundState getRoundStateForGame(Game game) {
        return getRoundState(status.getGameData(game));
    }

    /**
     * Returns the current reason to wait for the given game to continue.
     * @param  game game
     * @return wait reason
     */
    public WaitReason getWaitReasonForGame(Game game) {
        return getWaitReason(status.getGameData(game));
    }

    /**
     * Returns the current reason for no game for the given game.
     * @param  game game
     * @return no game reason
     */
    public NoGameReason getNoGameReasonForGame(Game game) {
        return getNoGameReason(status.getGameData(game));
    }

    /**
     * Returns the formatted current countdown value for the given game.
     * @param  game game
     * @return formatted countdown value
     */
    public String getCountDownForGame(Game game) {
        GameData data = status.getGameData(game);
        return data == null ? "--:--" : data.countDown.getCountDown();
    }

    /**
     * Returns the numeric current countdown value for the given game.
     * @param  game game
     * @return numeric countdown value
     */
    public int getTimerForGame(Game game) {
        GameData data = status.getGameData(game);
        return data == null ? 0 : data.countDown.getTimer();
    }

    /**
     * Method to calculate the points the given team has reached in the given game.
     * @param  game game
     * @param  team of which points are to be estimated
     * @return points
     */
    public Integer getPointsOfTeamForGame(Game game, Team team) {
        return game == null || team == null ? 0 : roundService.getPointsOfTeamInGame(game, team);
    }

    /**
     * Method to check which team has won the current game.
     * @param  game game
     * @return winning team
     */
    public Team getTeamWithMostPointsForGame(Game game) {
        return game == null ? null : gameLogicService.getTeamWithMostPoints(game);
    }

    /**
     * Method to add the given game to the saved list of currently available
     * games and send notifications to all participating team members.
     * @param game game
     * @throws IllegalArgumentException if game is null, not in state 
     * {@link GameState#SETUP} or if set cube is already associated with another game
     */
    public void addGame(Game game) {
        // add game to internal list and change game status
        if (status.addGame(game)) {

            // send invitation to all contained team members
            websocketManager.getMessageChannel().send(
                Map.of("type", "gameInvitation", "name", game.getName(), "id", game.getId()), getAllUserIdsOfGameTeams(game));
        }
    }

    /**
     * Method to halt the given game, i.e. keep it in the saved list of currently
     * available games and send paused notifications to all participating team members.
     * @param  game game
     * @throws IllegalArgumentException if given game is not in state {@link GameState#HALTED}
     */
    public void haltGame(Game game) {
        adaptInnerGameState(game, GameState.HALTED, "halting");
    }

    /**
     * Method to remove the given game from the saved list of currently
     * available games and send notifications to all participating team members.
     * @param  game game
     * @throws IllegalArgumentException if given game is not in state {@link GameState#CANCELED}
     */
    public void removeGame(Game game) {
        adaptInnerGameState(game, GameState.CANCELED, "removing");
    }

    /**
     * Method to revive the given game after it was in state {@link GameState#HALTED}
     * and send notifications to all participating team members.
     * @param  game game
     * @throws IllegalArgumentException if game is not in state {@link GameState#VALID_SETUP}
     */
    public void reviveGame(Game game) {
        checkGameState(game, GameState.VALID_SETUP, "reviving");

        if (getWaitReasonForGame(game) == WaitReason.GAME_HALTED) {
            GameData data = status.getGameData(game);
            data.game.setStatus(GameState.VALID_SETUP);
            switchGameState(data);

            if (data.game.getStatus() == GameState.VALID_SETUP)
                pauseGame(data);
        }
    }

    /**
     * Method to validate and save a round of a game.
     * Checks if a team reached the maximum of points or if all enabled terms of topic have been used.
     * Send corresponding message to all users in the game
     * @param game game
     * @param v validation of round
     */
    public void validateRoundOfGame(Game game, Validation v) {
        // end validation phase without checking the game status, as this leads to less problems
        // - players can evaluate the round as incorrect themselves
        // - by throwing the cube while the game is paused a running round can be invalidated
        GameData data = status.getGameData(game);

        if (getRoundState(data) == RoundState.VALIDATING) {
            gameLogicService.validateRound(data.game, data.currentRound, v);
            status.saveGame(data.game);

            Round lastRound = data.currentRound;
            switchRoundState(data, RoundState.NONE);

            if (gameLogicService.teamReachedMaxPoints(data.game, lastRound.getGuessingTeam())) {
                endGameFinished(data);
            }
            else if (gameLogicService.stillTermsAvailable(data.game)) {
                endRoundValidated(data);
            }
            else {
                endGameTermsOver(data);
            }
        }
    }

    /**
     * called by {@link CubeStatusController} if Cube is offline -&gt; GameStatus should be put to {@link GameState#VALID_SETUP}
     * @param cube cube which is offline
     */
    public void cubeOffline(Cube cube) {
        switchGameState(status.setCubeState(cube, false));
    }

    /**
     * called by {@link CubeStatusController} if Cube is online -&gt; GameStatus should be put to PLAYED if otherwise appropriate
     * @param cube cube which is online
     */
    public void cubeOnline(Cube cube) {
        switchGameState(status.setCubeState(cube, true));
    }

    /**
     * called by {@link CubeStatusController} if there is a health status to report i.e. low battery,
     * no connection puts current game on halted if cube is OFFLINE
     * @param cube cube to report to the current game
     */
    public void healthNotification(Cube cube) {
        sendHealthNotification(status.getGameData(cube));
    }

    /**
     * Method to put all usernames of players, that are online, into a list
     * @param  game game
     * @return list of user ids
     */
    private Set<Long> getAllUserIdsOfGameTeams(Game game) {
        return game.getTeams().stream()
            .flatMap(t -> t.getTeamMembers().stream().map(User::getId))
            .collect(Collectors.toSet());
    }

    private RoundState getRoundState(GameData data) {
        if (data == null || data.currentRound == null) return RoundState.NONE;
        if (!data.isRoundActive && !data.isValidating) return RoundState.STARTING;
        if (data.isRoundActive && !data.isValidating) return RoundState.RUNNING;
        if (!data.isRoundActive && data.isValidating) return RoundState.VALIDATING;

        return RoundState.ERROR;
    }

    private WaitReason getWaitReason(GameData data) {
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

    private NoGameReason getNoGameReason(GameData data) {
        if (data == null || data.game == null) return NoGameReason.GAME_UNKNOWN;

        switch (data.game.getStatus()) {
            case FINISHED: return NoGameReason.GAME_FINISHED;
            case CANCELED: return NoGameReason.GAME_CANCELED;
            case SETUP: return NoGameReason.GAME_WRONGSTATE;
            default: return NoGameReason.NONE;
        }
    }

    private void adaptInnerGameState(Game fromGame, GameState toState, String forWhat) {
        checkGameState(fromGame, toState, forWhat);

        GameData data = status.getGameData(fromGame);
        if (data != null) { switchGameState(data, data.game.getStatus(), toState); data.game.setStatus(toState); }
    }

    /**
     * Switches the state of the given game according to its inner settings
     * and acts to a change if necessary.
     * @param data
     */
    private void switchGameState(GameData data) {
        if (data == null) return;

        switchGameState(data, data.game.getStatus(), status.switchGameState(data));
    }

    /**
     * Sends notifications according to the given game states, if necessary.
     * @param data     game data
     * @param oldState old game state
     * @param newState new game state
     */
    private void switchGameState(GameData data, GameState oldState, GameState newState) {
        if (data == null) return;

        switch (newState) {
            case PLAYED:
                if (oldState != GameState.PLAYED) {
                    switch (getRoundState(data)) {
                        case NONE:
                        case ERROR:
                            startGame(data);
                            break;

                        default:
                            restartGame(data);
                            break;
                    }
                }
                break;

            case CANCELED:
                endGameCanceled(data);
                break;

            default:
                if (oldState != newState) pauseGame(data);
                break;
        }
    }

    /**
     * Sets the appropriate state attributes for the given state to the given data.
     * @param data
     * @param newState
     */
    private void switchRoundState(GameData data, RoundState newState) {
        if (data == null) return;

        switch (newState) {
            case STARTING:
                data.countDown.reset();
                data.currentRound = getRoundState(data) == RoundState.ERROR
                    ? gameLogicService.repeatRound(data.game, data.currentRound)
                    : gameLogicService.getNextRound(data.game);
                data.isRoundActive = false;
                data.isValidating = false;
                break;

            case RUNNING:
                data.startCountDown();
                data.isRoundActive = true;
                data.isValidating = false;
                break;

            case VALIDATING:
                data.countDown.endCountDown();
                data.isRoundActive = false;
                data.isValidating = true;
                break;

            case NONE:
                data.currentRound = null;
                data.isRoundActive = false;
                data.isValidating = false;
                break;

            case ERROR:
                data.isRoundActive = true;
                data.isValidating = true;
                break;
        }
    }

    private void checkGameState(Game game, GameState state, String forDoing) {
        if (game.getStatus() != state)
            throw new IllegalArgumentException(String.format(
                "Game %s is in state %s, must be in state %s for %s", game, game.getStatus(), state, forDoing));
    }

    /* --- START - push message sending functions --- */
    /**
     * Method to get informations of next round, before cube starts the round
     * @param data data of game for which informations of next round should be estimated
     */
    private void startGame(GameData data) {
        switchRoundState(data, RoundState.STARTING);
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
        Team winningTeam = gameLogicService.getTeamWithMostPoints(data.game);
        data.game.setMaxPoints(roundService.getPointsOfTeamInGame(data.game, winningTeam));

        status.endGame(data, true);
        sendGameMessage(data.game, "termsOver");
    }

    /**
     * Method that starts a new Round by the cube for a game.
     * It initializes a new round and also calls a method to start the countdown.
     * @param data data of game for which round should be started
     * @param cubeFace face that sets round parameters
     */
    private void startRoundByCube(GameData data, CubeFace cubeFace) {
        gameLogicService.getCubeInfosIntoRound(data.currentRound, cubeFace);
        switchRoundState(data, RoundState.RUNNING);

        sendGameMessage(data.game, "startRound");
    }

    private void endRoundByCube(GameData data, CubeFace cubeFace) {
        switchRoundState(data, RoundState.VALIDATING);
        sendGameMessage(data.game, "endRoundViaFlip");
    }

    private void endRoundByCountDown(GameData data) {
        // send countdown ended only on first call
        if (getRoundState(data) == RoundState.RUNNING) {
            switchRoundState(data, RoundState.VALIDATING);
            sendGameMessage(data.game, "endRoundViaCountDown");
        }
    }

    /**
     * Method to get informations of next round, before cube starts the round
     * @param data data of game for which informations of next round should be estimated
     */
    private void endRoundValidated(GameData data) {
        switchRoundState(data, RoundState.STARTING);
        sendGameMessage(data.game, "validatedRound");
    }

    public void sendHealthNotification(GameData data) {
        if (data != null) sendGameMessage(data.game, "healthMessage");
    }

    private void sendGameMessage(Game game, String message) {
        if (game != null) {
            this.websocketManager.getNewRoundChannel().send(message, getAllUserIdsOfGameTeams(game));
        }
    }

    private void sendCountDownUpdate(Game game) {
        if (game != null) {
            this.websocketManager.getCountDownChannel().send("countDownUpdate", getAllUserIdsOfGameTeams(game));
        }
    }
    /* --- END - push message sending functions --- */

    class Status implements Iterable<GameData> {

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
            GameState oldState = data.game.getStatus();
            switch (oldState) {
                case VALID_SETUP:
                case PLAYED:
                    GameState newState = data.isTeamsPresent && data.isCubeOnline ? GameState.PLAYED
                        : GameState.VALID_SETUP;
                    Game game = setGameState(data.game, newState);
                    // would cause problems: if (game != null) data.game = game;
                    assert game == null && data.game.getStatus() == oldState || data.game.getStatus() == newState;
                    return newState;

                default:
                    return oldState;
            }
        }

        /**
         * Adds the given game to the inner list, sets its state to {@link GameState#VALID_SETUP} and saves it.
         * @param  game
         * @throws IllegalArgumentException if game is null
         * @throws IllegalArgumentException if game is not in state {@link GameState#SETUP}
         * @throws IllegalArgumentException if set cube is already associated with another game
         */
        @SuppressWarnings("unlikely-arg-type")
        public boolean addGame(Game game) {
            if (game == null)
                throw new IllegalArgumentException("Game must not be null");

            // check correct initial state
            checkGameState(game, GameState.SETUP, "adding");

            // check cube already in use
            Cube cube = game.getCube();
            if (cube2Game.containsKey(cube) && !cube2Game.get(cube).equals(game))
                throw new IllegalArgumentException(
                    String.format("Cube %s is already in use for game %s", cube, cube2Game.get(cube)));

            // save new gamestate, on error revert state
            Game ret = setGameState(game, GameState.VALID_SETUP);
            if (ret == null) return false;

            // put into internal list tied to cube
            cube2Game.put(cube, new GameData(game, GameManagerController.this.new CountDown()));
            return true;
        }

        /**
         * Ends the game. Sets its status to finished, saves it in the database
         * and removes it from the inner list.
         * @param data game that finishes
         */
        public void endGame(GameData data, boolean finished) {
            setGameState(data.game, finished ? GameState.FINISHED : GameState.CANCELED);
            gameTeamService.updatePoints(data.game);
            data.countDown.reset();
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
            if (game == null) return null;

            // have to check if this is the correct game
            // because it might be an old game having the same cube still set
            GameData data = getGameData(game.getCube(), false);
            return data == null || !data.game.equals(game) ? null : reload ? reload(data) : data;
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

            // transfer relevant (possibly changed outside) values from fresh instance to saved one,
            // the other way around (i.e. replacing current instance) leads to different kinds of problems
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
         * Tries to save the given game after making sure authentication is set.
         * @param  game
         * @return saved game
         */
        private Game saveGame(Game game) {
            setAuthenticatedUserDefault();
            return gameService.saveGame(game);
        }

        /**
         * Tries to save given game with given state, on error resets old state in instance.
         * @param  game  game to save with given state
         * @param  state state to save in given game
         * @return saved game or null if given game could not be saved, given game if state is already set
         */
        private Game setGameState(Game game, GameState newState) {
            GameState oldState = game.getStatus();
            if (oldState != newState) {
                game.setStatus(newState);
                Game ret = saveGame(game);
                if (ret == null) game.setStatus(oldState);
                return ret;
            }
            return game;
        }

        private void setAuthenticatedUserDefault() {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication auth = context.getAuthentication();
            if (auth == null) {
                auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(DEFAULT_USER, DEFAULT_PASSWORD));
                context.setAuthentication(auth);
            }
        }
    }

    /**
     * Class holding running game info.
     */
    public static class GameData {

        final CountDown countDown;
        final Game game;
        Round currentRound = null;
        boolean isRoundActive = false;
        boolean isValidating = false;
        boolean isTeamsPresent = false; // assume cube is online (otherwise would have to wait for online-message)
        boolean isCubeOnline = true;

        /**
         * Initializes a game info instance.
         * @param  game game
         * @param  countDown countDown
         * @throws IllegalArgumentException if game is null
         */
        public GameData(Game game, CountDown countDown) {
            if (game == null) throw new IllegalArgumentException("Cannot hold null game");
            this.game = game;
            this.countDown = countDown;
        }

        /**
         * Starts a countdown for the held game with the time set in the current round.
         */
        public void startCountDown() {
            countDown.startCountDown(currentRound.getTime(), this);
        }

        /**
         * Calculates the number of present teams from the given list of user ids
         * and sets its internal field accordingly.
         * @param userIds set of user ids
         */
        public void updateTeamPresence(Set<Long> userIds) {
            // count present teams (one player per team must be present)
            long count = game.getTeams().stream()
                .filter(t -> t.getTeamMembers().stream()
                .anyMatch(u -> userIds.contains(u.getId())))
                .count();

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

            if (obj.getClass() == Game.class) {
                final Game other = (Game) obj;
                return Objects.equals(game, other);
            }

            if (obj.getClass() == GameData.class) {
                final GameData other = (GameData) obj;
                return Objects.equals(game, other.game);
            }

            return false;
        }
    }

    public class CountDown {

        private int min = 0;
        private int sec = 0;
        private Timer timer;
        private int delay = 1000; // milliseconds

        /**
         * @return countDown as string to display
         */
        public String getCountDown() {
            return getMin() + " : " + getSec();
        }

        /**
         * @return countDown as integer
         */
        public int getTimer() {
            return getMin() * 60 + getSec();
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getSec() {
            return sec;
        }

        public void setSec(int sec) {
            this.sec = sec;
        }

        /**
         * starts countDown with given time and for given game
         * @param time to guess
         * @param gameData game for which the countdown starts
         */
        public void startCountDown(int time, GameData gameData) {
            reset(time);

            timer = new Timer(delay, e -> count(gameData));
            timer.setInitialDelay(1200);
            timer.start();
        }

        /**
         * stops countDown
         */
        public void endCountDown() {
            if (timer != null) {
                timer.stop();
            }
        }

        /**
         * resets countdown to 0
         */
        public void reset() {
            reset(0);
        }

        /**
         * counting step - informs websocket listener after each update
         * @param gameData game for which the countdown runs
         */
        public void count(GameData gameData) {
            if (min > 0) {
                if (sec > 0) {
                    sec--;
                }
                else {
                    min--;
                    sec = 59;
                }
            }
            else if (min == 0 && sec > 0) {
                sec--;
            }
            else {
                endCountDown();
                // put the round inactive to enable starting a new round via flip
                GameManagerController.this.endRoundByCountDown(gameData);
            }
            GameManagerController.this.sendCountDownUpdate(gameData.game);
        }

        private void reset(int min) {
            if (timer != null) {
                if (timer.isRunning()) timer.stop();
                timer.removeActionListener(timer.getActionListeners()[0]);
                timer = null;
            }
            setMin(min);
            setSec(0);
        }
    }
}
