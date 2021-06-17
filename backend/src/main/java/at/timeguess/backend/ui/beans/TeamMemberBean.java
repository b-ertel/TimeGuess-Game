package at.timeguess.backend.ui.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;

/**
 * Bean for JSF specific team member view.
 * Loads information for teams in a given game or all teams.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TeamMemberBean implements Serializable {

    private static final long serialVersionUID = -3429788166387415247L;

    @Autowired
    private GameService gameService;
    @Autowired
    private TeamService teamService;

    /**
     * Attributes to cache the currently displayed items
     */
    private Game game;
    private Team team;

    /**
     * Sets the currently displayed game.
     * @param game game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets the currently selected team.
     * @param team team
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Returns the currently selected team.
     * @return team
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Returns the currently displayed teams.
     * If game is not set this means all teams.
     * @return list of {@link SelectItem}s
     */
    public List<SelectItem> getTeams() {
        return game == null ? createAll() : create(game);
    }

    public void doReloadGame() {
        if (game != null) {
            game = gameService.loadGame(game.getId());
        }
    }

    private List<SelectItem> create(Game game) {
        Set<Team> teams = game.getTeams();
        return teams == null ? null : createTeamMembers(teams);
    }

    private List<SelectItem> createAll() {
        return createTeamMembers(teamService.getAllTeams());
    }

    private static List<SelectItem> createTeamMembers(Collection<Team> teams) {
        List<SelectItem> uiteams = teams.stream().map(t -> {
            SelectItemGroup ret = new SelectItemGroup(t.getName());
            ret.setValue(t);
            if (t.getTeamMembers() != null)
                ret.setSelectItems(t.getTeamMembers().stream()
                    .map(u -> new SelectItem(u, u.getUsername()))
                    .toArray(SelectItem[]::new));
            return ret;
        }).collect(Collectors.toList());
        return uiteams;
    }
}
