package at.timeguess.backend.ui.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.GameService;

/**
 * Bean for JSF specific game info view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class GameInfoBean implements Serializable {

    private static final long serialVersionUID = -3429788166387415247L;

    @Autowired
    private GameService gameService;

    /**
     * Attributes to cache the currently displayed items
     */
    private Game game;

    /**
     * Sets the currently displayed game.
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Returns the rounds of the set game.
     * @param game
     */
    public Set<Round> getRounds() {
        return game == null ? null : game.getRounds();
    }

    /**
     * Returns the team infos for the set game.
     * @param game
     */
    public TreeNode getTeams() {
        return game == null ? new DefaultTreeNode() : createGameInfos(game);
    }

    public void doReloadGame() {
        if (game != null) {
            game = gameService.loadGame(game.getId());
        }
    }

    private static TreeNode createGameInfos(Game game) {
        Set<Round> rounds = game.getRounds();
        Set<Team> teams = game.getTeams();

        // helper maps: all teams, all players, all team/playerIds
        Map<Long, GameInfo> mteams = new TreeMap<>();
        Map<Long, GameInfo> mplayers = new TreeMap<>();
        Map<Long, Set<Long>> mplayerIds = new HashMap<>();

        // fill helpers maps with base data
        teams.stream().forEach(t -> {
            // team
            mteams.put(t.getId(), new GameInfo(t.getId(), t.getName(), 0, 0));
            // players
            Set<Long> playerIds = new HashSet<>();
            t.getTeamMembers().forEach(u -> {
                playerIds.add(u.getId());
                mplayers.put(u.getId(), new GameInfo(u.getId(), u.getUsername(), 0, 0));
            });
            mplayerIds.put(t.getId(), playerIds);
        });

        // complete with round and point info
        GameInfo gameinfo = new GameInfo(game.getId(), game.getName(), 0, 0);
        rounds.stream().forEach(r -> {
            gameinfo.addRound();
            gameinfo.addPoints(r.getPoints());

            addGameInfo(mteams, r, r.getGuessingTeam().getId());
            addGameInfo(mplayers, r, r.getGuessingUser().getId());
        });

        // create treenodes from maps
        TreeNode root = new DefaultTreeNode(gameinfo, null);
        var eplayers = mplayers.entrySet();
        mteams.values().forEach(teaminfo -> {
            TreeNode tn = new DefaultTreeNode(teaminfo, root);
            eplayers.stream().filter(e -> mplayerIds.get(teaminfo.getId()).contains(e.getKey()))
                .forEach(e -> new DefaultTreeNode(e.getValue(), tn));
        });

        return root;
    }

    private static void addGameInfo(Map<Long, GameInfo> map, Round round, Long id) {
        if (map.containsKey(id)) {
            GameInfo curr = map.get(id);
            curr.addRound();
            curr.addPoints(round.getPoints());
        }
    }

    public static class GameInfo implements Comparable<GameInfo> {

        private Long id;
        private String name;
        private Integer rounds;
        private Integer points;

        public GameInfo(Long id, String name, int rounds, int points) {
            this.id = id;
            this.name = name;
            this.rounds = rounds;
            this.points = points;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRounds() {
            return rounds.toString();
        }

        public void setRounds(int rounds) {
            this.rounds = rounds;
        }

        public String getPoints() {
            return points.toString();
        }

        public void setPoints(int points) {
            this.points = points;
        }

        void addRound() {
            rounds += 1;
        }

        void addPoints(int points) {
            this.points += points;
        }

        @Override
        public int compareTo(GameInfo o) {
            return name.compareTo(o.getName());
        }
    }
}
