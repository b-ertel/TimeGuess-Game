package at.timeguess.backend.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.spring.CDIAwareBeanPostProcessor;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * Service for accessing and manipulating user data.
 */
@Component
@Scope("application")
@CDIContextRelated
public class RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @CDIAutowired
    private WebSocketManager websocketManager;

    /**
     * @apiNote neither {@link Autowired} nor {@link CDIAutowired} work for a {@link Component},
     * and {@link javax.annotation.PostConstruct} is not invoked, so autowiring is done manually
     */
    public RoundService() {
        if (websocketManager == null) {
            new CDIAwareBeanPostProcessor().postProcessAfterInitialization(this, "websocketManager");
        }
    }

    /**
     * Returns a collection of all rounds.
     * @return collection of rounds
     */
    public Collection<Round> getAllRounds() {
        return roundRepository.findAll();
    }

    /**
     * method to estimate last round of a game
     * @param  game game
     * @return last round of game
     */
    public Round getLastRound(Game game) {
        List<Round> ret = roundRepository.getRoundOfGame(game);
        return ret.isEmpty() ? null : ret.get(ret.size() - 1);
    }

    /**
     * method to check whether rounds where played in game or not
     * @param  game game
     * @return true if yes, false if no
     */
    public boolean roundsPlayedInGame(Game game) {
        return (roundRepository.getRoundOfGame(game).size() != 0);
    }

    /**
     * Method to estimate which round was the last round, a certain team has played in a game
     * @param  game game that team played
     * @param  team team of which last round should be evaluated
     * @return last round of the team in the game
     */
    public Round getLastRoundOfTeam(Game game, Team team) {
        List<Round> ret = roundRepository.getRoundOfGameWithTeam(game, team);
        return ret.isEmpty() ? null : ret.get(ret.size() - 1);
    }

    /**
     * check whether team played rounds in game
     * @param  game game
     * @param  team team
     * @return true if it did, false if not
     */
    public boolean teamPlayedRoundsInGame(Game game, Team team) {
        return (roundRepository.getRoundOfGameWithTeam(game, team).size() != 0);
    }

    /**
     * Method to estimate how many points a team has reached in a certain game
     * @param  game game
     * @param  team team
     * @return points of team in game
     */
    public Integer getPointsOfTeamInGame(Game game, Team team) {
        Integer ret = roundRepository.getPointsOfTeamInGame(game, team);
        return ret == null ? 0 : ret;
    }

    /**
     * Saves the round.
     * Additionally fills gui message with success or failure info and triggers a push update.
     * @param   round the round to save
     * @return  the saved round
     * @apiNote Message handling ist done here, because this is the central place for saving terms.
     */
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('ADMIN')")
    public Round saveRound(Round round) {
        Round ret = null;
        try {
            ret = roundRepository.save(round);

            if (websocketManager != null)
                websocketManager.getUserRegistrationChannel().send(
                    Map.of("type", "roundUpdate", "name", String.valueOf(round.getNr()), "id", round.getId()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
