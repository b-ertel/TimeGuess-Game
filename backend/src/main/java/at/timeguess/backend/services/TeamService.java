package at.timeguess.backend.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.TeamRepository;
import at.timeguess.backend.ui.beans.MessageBean;

@Component
@Scope("application")
public class TeamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MessageBean messageBean;

    /**
     * Returns a list of all teams.
     * @return
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Returns a list of teams currently not playing.
     * @param team
     * @return
     */
    public List<Team> getAvailableTeams() {
        return teamRepository.findAvailableTeams();
    }

    /**
     * Returns the team for the given id.
     * @param id
     * @return
     */
    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    /**
     * Returns the team for the given name.
     * @param name
     * @return
     */
    public Team loadTeam(String name) {
        return teamRepository.findFirstByName(name);
    }

    /**
     * Checks if the given team is currently playing or not.
     * @param team
     * @return
     */
    public boolean isAvailableTeam(Team team) {
        return teamRepository.getIsAvailableTeam(team);
    }

    /**
     * Checks if the given team is currently playing in any other than the given game or not.
     * @param team
     * @param game
     * @return
     */
    public boolean isAvailableTeamForGame(Team team, Game game) {
        return teamRepository.getIsAvailableTeamForGame(team, game);
    }

    /**
     * Saves the given team.
     * @param team the team to save
     * @return the saved team
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('ADMIN')")
    public Team saveTeam(Team team) {
        boolean isNew = team.isNew();

        Team ret = teamRepository.save(team);

        // show ui message and log
        messageBean.alertInformation(ret.getName(), isNew ? "New team created" : "Team updated");

        LOGGER.info("Team '{}' (id={}) was {}", ret.getName(), ret.getId(), isNew ? "created" : "updated");

        return ret;
    }
}
