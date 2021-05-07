package at.timeguess.backend.ui.controllers;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This controller is responsible for showing a term in the game window with websockets.
 */
@Controller
@Scope("application")
@CDIContextRelated
public class GameMangerController implements Consumer<ConfiguredFacetsEvent> {

    @Autowired
    private GameService gameService;
    @Autowired
    private CubeService cubeService;
    @CDIAutowired
    private WebSocketManager websocketManager;
    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @Autowired
    private GameLogicService gameLogic;

    private Map<Game, Round> currentRound = new ConcurrentHashMap<>();
    private Round controllRound;

    private Map<Cube, Game> listOfGames = new HashMap<>();

    @PostConstruct
    public void setup() {
        configuredfacetsEventListener.subscribe(this);
        Game testgame1 = gameService.loadGame(8L);
        Cube cube = cubeService.getByMacAddress("56:23:89:34:56");

        System.out.println("players for testgame 1:");
        for (Team t : testgame1.getTeams()) {
            for (User u : t.getTeamMembers()) {
                System.out.println(u.getUsername());
            }
        }

        Game testgame2 = gameService.loadGame(9L);
        Cube cube2 = cubeService.getByMacAddress("22:23:89:90:56");

        System.out.println("players for testgame 2:");
        for (Team t : testgame2.getTeams()) {
            for (User u : t.getTeamMembers()) {
                System.out.println(u.getUsername());
            }
        }

        listOfGames.put(cube, testgame1);
        listOfGames.put(cube2, testgame2);
        this.controllRound = new Round();
    }

    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     * Method is called on facet-event, checks to which game it corresponds and calls startNewRound() to initialize new round.
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (listOfGames.keySet().contains(configuredFacetsEvent.getCube())) {
            startNewRound(listOfGames.get(configuredFacetsEvent.getCube()), configuredFacetsEvent.getCubeFace());
        }
    }

    public Round getControllRound() {
        return controllRound;
    }

    public Round getCurrentRoundOfGame(Game game) {
        return this.currentRound.get(game);
    }

    /**
     * method to get the current round for a given user, called by {@link UserGameController}
     * @param user to find current round
     * @return current round
     */
    public Round getCurrentRoundForUser(User user) {
        Round round = new Round();

        for (Map.Entry<Game, Round> e : currentRound.entrySet()) {
            for (Team t : e.getKey().getTeams()) {
                if (t.getTeamMembers().contains(user)) {
                    round = e.getValue();
                }
            }
        }
        return round;
    }

    /**
     * A method to put all usernames of players, that are online, into a list
     * @param listOfTeams
     * @return list of usernames
     */
    private List<Long> getAllUserIdsOfGameTeams(Set<Team> listOfTeams) {
        List<Long> userIds = new ArrayList<>();
        List<User> users = new ArrayList<>();
        for (Team team : listOfTeams) {
            users.addAll(team.getTeamMembers());
        }
        users.stream().forEach(user -> userIds.add(user.getId()));
        return userIds;
    }

    /**
     * Method to add a newly created game to the saved list of currently available games
     * and send notifications to all participating team members, except the game creator.
     * @param game
     */
    public void addGame(Game game) {
        if (game == null) throw new NullPointerException("startGame was called with null game");

        // add game to map
        listOfGames.put(game.getCube(), game);

        // send invitation to all contained team members except host
        final User host = game.getCreator();
        Set<Long> invited = game.getTeams().stream()
                .flatMap(t -> t.getTeamMembers().stream().filter(u -> u != host).map(User::getId)).collect(Collectors.toSet());
        websocketManager.getMessageChannel().send(Map.of("type", "gameInvitation", "name", game.getName(), "id", game.getId()), invited);
    }

    /**
     * Method that starts a new Round for a game. It initializes a new round
     * and also calls a method to start the countdown.
     * @param currentGame, game for which round should be started
     * @param cubeFace,    face that sets round parameter
     */
    public void startNewRound(Game currentGame, CubeFace cubeFace) {
        this.currentRound.put(currentGame, gameLogic.startNewRound(currentGame, cubeFace));
        this.websocketManager.getNewRoundChannel().send("newRound", getAllUserIdsOfGameTeams(currentGame.getTeams()));
    }
}
