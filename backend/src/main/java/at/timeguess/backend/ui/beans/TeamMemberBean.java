package at.timeguess.backend.ui.beans;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;

/**
 * Bean for JSF specific team member view.
 */
@Component
@Scope(WebApplicationContext.SCOPE_SESSION)
public class TeamMemberBean implements Serializable {

    private static final long serialVersionUID = -3429788166387415247L;

    /**
     * Attributes to cache the currently displayed items
     */
    private Game game;
    private Team team;
    private List<SelectItem> teams;

    /**
     * Sets the currently displayed game.
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
        this.init();
    }

    /**
     * Sets the currently selected team.
     * @param game
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Returns the currently selected team.
     * @return
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Returns the currently displayed teams.
     * @param game
     */
    public List<SelectItem> getTeams() {
        return teams;
    }

    private void init() {
        teams = game == null || game.getTeams() == null ? null : game.getTeams().stream().map(t -> {
            SelectItemGroup ret = new SelectItemGroup(t.getName());
            ret.setValue(t);
            if (t.getTeamMembers() != null)
                ret.setSelectItems(t.getTeamMembers().stream().map(u -> new SelectItem(u, u.getUsername()))
                    .toArray(SelectItem[]::new));
            return ret;
        }).collect(Collectors.toList());
    }
}
