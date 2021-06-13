package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.TeamRepository;
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating team data.
 */
@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
@CDIContextRelated
public class TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MessageBean messageBean;

    @CDIAutowired
    private WebSocketManager websocketManager;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link javax.annotation.PostConstruct} is not invoked, so autowiring is done manually
     */
    public TeamService() {
        if (websocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "websocketManager");
        }
    }

    /**
     * Returns a list of all teams.
     * @return list of teams
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Returns a list of teams currently not playing.
     * @return list of teams
     */
    public List<Team> getAvailableTeams() {
        return teamRepository.findAvailableTeams();
    }

    /**
     * Returns the team for the given id.
     * @param id team id
     * @return team
     */
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    /**
     * Returns the team for the given name.
     * @param name of team
     * @return team
     */
    public Team loadTeam(String name) {
        return teamRepository.findFirstByName(name);
    }

    /**
     * Checks if the given team is currently playing or not.
     * @param team team
     * @return true if it is, false if not
     */
    public boolean isAvailableTeam(Team team) {
        return teamRepository.getIsAvailableTeam(team);
    }

    /**
     * Checks if the given team is currently playing in any other than the given game or not.
     * @param team team
     * @param game game
     * @return true if it is, false if not
     */
    public boolean isAvailableTeamForGame(Team team, Game game) {
        return teamRepository.getIsAvailableTeamForGame(team, game);
    }

    /**
     * Saves the given team.
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param team the team to save
     * @return the saved team
     * @apiNote Message handling ist done here, because this is the central place for saving terms.
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('ADMIN')")
    public Team saveTeam(Team team) {
        Team ret = null;
        try {
            boolean isNew = team.isNew();

            ret = teamRepository.save(team);

            // show ui message and log
            messageBean.alertInformation(ret.getName(), isNew ? "New team created" : "Team updated");

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                        Map.of("type", "teamUpdate", "name", team.getName(), "id", team.getId()));

            LOGGER.info("Team '{}' (id={}) was {}", ret.getName(), ret.getId(), isNew ? "created" : "updated");
        }
        catch (Exception e) {
            String msg = "Saving team failed";
            if (e.getMessage().contains("TEAM(NAME)"))
                msg += String.format(": team named '%s' already exists", team.getName());
            messageBean.alertErrorFailValidation(team.getName(), msg);

            LOGGER.info("Saving team '{}' (id={}) failed, stack trace:", team.getName(), team.getId());
            e.printStackTrace();
        }
        return ret;
    }
}
