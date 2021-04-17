package at.timeguess.backend.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Round;
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
}
