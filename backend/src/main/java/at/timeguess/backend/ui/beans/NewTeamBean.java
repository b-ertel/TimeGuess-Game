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

import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.services.UserService;

/**
 * Bean for creating a new team.
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class NewTeamBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TeamService teamService;
    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;

    private String teamname;
    private Set<User> players = new HashSet<>();

    public String getTeamName() {
        return teamname;
    }

    public void setTeamName(String name) {
        this.teamname = name;
    }

    public Set<User> getPlayers() {
        return players;
    }

    public void setPlayers(Set<User> players) {
        this.players = players;
    }

    /**
     * Returns a list of all players.
     * @return
     */
    public List<User> getAllPlayers() {
        return userService.getAllPlayers();
    }

    /**
     * Checks if the given user is currently playing or not.
     * @param user
     * @return
     */
    public boolean isAvailablePlayer(User user) {
        return userService.isAvailablePlayer(user);
    }

    /**
     * Clears all fields.
     */
    public void clearFields() {
        this.setTeamName(null);
        this.setPlayers(new HashSet<>());
    }

    /**
     * Creates a new team with the settings saved.
     * @apiNote shows a ui message if input fields are invalid.
     */
    public Team createTeam() {
        if (this.validateInput()) {
            Team team = new Team();

            team.setName(teamname);
            team.setTeamMembers(players);

            team = teamService.saveTeam(team);
            if (team != null) {
                this.clearFields();
            }

            return team;
        }
        else {
            messageBean.alertErrorFailValidation("Team creation failed", "Input fields are invalid");
        }
        return null;
    }

    /**
     * Checks if all fields contain valid values.
     * @return
     */
    public boolean validateInput() {
        if (Strings.isBlank(teamname)) return false;
        if (players == null || players.size() < 2) return false;
        return true;
    }
}
