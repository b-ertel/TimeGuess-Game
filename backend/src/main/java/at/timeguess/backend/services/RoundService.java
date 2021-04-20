package at.timeguess.backend.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.RoundRepository;


/**
 * Service for accessing and manipulating user data.
 */
@Component
@Scope("application")
public class RoundService {
	
    @Autowired
    private RoundRepository roundRepository;

    /**
     * Returns a collection of all rounds.
     * @return
     */
    public Collection<Round> getAllRounds() {
        return roundRepository.findAll();
    }

    /**
     * Saves the round.
     * @param round the round to save
     * @return the saved round
     */
    public Round saveRound(Round round) {
        Round ret = roundRepository.save(round);
        return ret;
    }
    
    public Round getLastRound(Game game) {
    	if(roundRepository.getRoundOfGame(game).isEmpty()) {
    		return null;
    	}
    	return roundRepository.getRoundOfGame(game).get(roundRepository.getRoundOfGame(game).size()-1);
    }
    
    public boolean roundsPlayedInGame(Game game) {
    	return (roundRepository.getRoundOfGame(game).size()!=0);
    }
    
    public Round getLastRoundOfTeam(Game game, Team team) {
    	if(roundRepository.getRoundOfGameWithTeam(game, team).isEmpty()) {
    		return null;
    	}
    	return roundRepository.getRoundOfGameWithTeam(game, team).get(roundRepository.getRoundOfGameWithTeam(game, team).size()-1);
    }
    
    public boolean teamPlayedRoundsInGame(Game game, Team team) {
    	return (roundRepository.getRoundOfGameWithTeam(game, team).size()!=0);
    }
}
