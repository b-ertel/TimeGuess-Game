package at.timeguess.backend.ui.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
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
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.ui.controllers.GameManagerController;

/**
 * Bean for creating a new game.
 * @apiNote Scope is set to session for the view:
 * as the team creation button is in the same dialog (and this is because
 * doing team creation in a separate dialog never updated the teams list)
 * once the new team is created the game fields are emptied because a new
 * instance of both beans is created by the underlying system.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class NewGameBean implements Serializable {

    private static final long serialVersionUID = -4340754545184734831L;

    @Autowired
    private GameService gameService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private GameManagerController gameManagerController;

    @Autowired
    private MessageBean messageBean;

    private String gameName;
    private int maxPoints = 10;
    private Topic topic;
    private Cube cube;

    private Set<Team> teams = new HashSet<>();

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the maxPoints
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * @param maxPoints the maxPoints to set
     */
    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    /**
     * @return the associated teams
     */
    public Set<Team> getTeams() {
        return teams;
    }

    /**
     * @param teams teams to associate
     */
    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    /**
     * Returns a list of all teams.
     * @return
     */
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Checks if the given team is currently playing or not.
     * @param team
     * @return
     */
    public boolean isAvailableTeam(Team team) {
        return teamService.isAvailableTeam(team);
    }

    /**
     * Adds the given team to the saved list.
     * @param team
     */
    public void addNewTeam(Team team) {
        if (team != null) {
            Set<Team> tmp = teams == null ? new HashSet<>() : new HashSet<>(teams);
            tmp.add(team);
            this.setTeams(tmp);
        }
    }

    /**
     * Clears all fields.
     */
    public void clearFields() {
        this.setGameName(null);
        this.setMaxPoints(0);
        this.setTopic(null);
        this.setCube(null);
        this.setTeams(null);
    }

    /**
     * Creates a new game with the settings saved.
     * @apiNote shows a ui message if input fields are invalid.
     */
    public Game createGame() {
        if (this.validateInput()) {
            Game game = new Game();

            game.setName(gameName);
            game.setMaxPoints(maxPoints);
            game.setTopic(topic);
            game.setCube(cube);
            game.setTeams(teams);
            game.setStatus(GameState.SETUP);

            game = gameService.saveGame(game);
            
            if (game != null) gameManagerController.addGame(game);

            this.clearFields();
            return game;
        }
        else {
            messageBean.alertErrorFailValidation("Game creation failed", "Input fields are invalid");
        }
        return null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return
     */
    public boolean validateInput() {
        if (Strings.isBlank(gameName)) return false;
        if (maxPoints <= 0) return false;
        if (cube == null || cube.isNew()) return false;
        if (topic == null) return false;
        if (teams == null || teams.size() < 2) return false;
        return true;
    }
}
