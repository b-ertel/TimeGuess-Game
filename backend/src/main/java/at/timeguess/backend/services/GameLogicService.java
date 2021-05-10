package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.model.exceptions.AllTermsUsedInGameException;

@Component
@Scope("application")
public class GameLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameLogicService.class);

    @Autowired
    TermService termService;

    @Autowired
    RoundService roundService;

    /**
     * method to check whether all terms of a topic have been used or not
     * @param game
     * @return boolean whether there a still terms available or not
     */
    public boolean stillTermsAvailable(Game game) {
        if (usedTerms(game).size() == termService.getAllTermsOfTopic(game.getTopic()).size()) return false;
        else return true;
    }
    
    public boolean teamReachedMaxPoints(Game game, Team team) {
    	if(game.getMaxPoints()<=roundService.getPointsOfTeamInGame(game, team)) {
    		return true;
    	}
    	return false;
    }
    
    public Team getTeamWithMostPoints(Game game) {
    	Integer points = 0;
    	Team team = null;
    	for(Team t : game.getTeams()) {
    		if(roundService.getPointsOfTeamInGame(game, t)>points) {
    			team = t;
    			points = roundService.getPointsOfTeamInGame(game, t);
    		}
    	}
    	return team;
    }

    /**
     * method to estimate which terms have been used
     * @param game, current game
     * @return set with all terms, that have been used in the current game
     */
    public Set<Term> usedTerms(Game game) {
        Set<Term> usedTerms = new HashSet<>();
        game.getRounds().stream().forEach(round -> usedTerms.add(round.getTermToGuess()));
        return usedTerms;
    }

    /**
     * method to choose randomly a term from the one, that where not already picked in previous rounds
     * @param game
     * @return term to guess
     * @throws AllTermsUsedInGameException, if every term has been played
     */
    public Term nextTerm(Game game) {
        List<Term> terms = termService.getAllTermsOfTopic(game.getTopic());
        Set<Term> usedTerms = usedTerms(game);
        usedTerms.stream().forEach(term -> terms.remove(term));
        Random rand = new Random();
        return terms.get(Math.abs(rand.nextInt(terms.size())));
    }

    public Team getNextTeam(Game game) {
        List<Team> teams = new ArrayList<>(game.getTeams());
        if (roundService.roundsPlayedInGame(game)) {
            Team teamOfLastRound = roundService.getLastRound(game).getGuessingTeam();
            int index = 0;
            for (Team t : teams) {
                if (t.getName() == teamOfLastRound.getName()) {
                    index = teams.indexOf(t);
                }
            }
            if (index == (teams.size() - 1)) {
                return teams.get(0);
            }
            else {
                return teams.get(index + 1);
            }
        }
        else {
            Random rand = new Random();
            return teams.get(Math.abs(rand.nextInt(teams.size())));
        }
    }

    public User nextUser(Game game, Team team) {
        List<User> users = new ArrayList<>();
        Iterator<User> ite = team.getTeamMembers().iterator();
        while (ite.hasNext()) {
            users.add(ite.next());
        }
        if (roundService.teamPlayedRoundsInGame(game, team)) {
            User lastUser = roundService.getLastRoundOfTeam(game, team).getGuessingUser();
            int index = 0;
            for (User u : users) {
                if (u.getUsername() == lastUser.getUsername()) {
                    index = users.indexOf(u);
                }
            }
            if (index == (users.size() - 1)) {
                return users.get(0);
            }
            else {
                return users.get(index + 1);
            }
        }
        else {
            Random rand = new Random();
            return users.get(rand.nextInt(users.size()));
        }
    }

    public Round startNewRound(Game game, CubeFace cubeFace) {
		Round nextRound = new Round();
		nextRound.setNr(game.getRounds().size()+1);
		nextRound.setActivity(cubeFace.getActivity());
		Team nextTeam = getNextTeam(game);
		nextRound.setGuessingUser(nextUser(game, nextTeam));
		nextRound.setGuessingTeam(nextTeam);
		Set<Team> verifiyingTeams = game.getTeams();
		verifiyingTeams.remove(nextTeam);
		nextRound.setVerifyingTeams(verifiyingTeams);
		nextRound.setPoints(cubeFace.getPoints());
		nextRound.setTermToGuess(nextTerm(game));
		nextRound.setGame(game);
		nextRound.setTime(cubeFace.getTime());
		game.getRounds().add(nextRound);
		game.setRoundNr(game.getRoundNr()+1);
		LOGGER.info("New Round nr '{}', with team '{}' and user '{}' was created", nextRound.getNr(), nextRound.getGuessingTeam().getName(), nextRound.getGuessingUser().getUsername());
		return nextRound;
	}
    
    public void saveLastRound(Game game, Validation v) {
        Set<Round> rounds = game.getRounds();
        Round lastRound = null;
        Iterator<Round> ite = rounds.iterator();
        int highestNr = 0;
        while (ite.hasNext()) {
            Round aRound = ite.next();
            if (aRound.getNr() > highestNr) {
                highestNr = aRound.getNr();
                lastRound = aRound;
            }
        }
        if (lastRound != null) {
        	game.getRounds().remove(lastRound);
        	if(v == Validation.CORRECT) {
        		lastRound.setCorrectAnswer(true);
        	} else if (v == Validation.INCORRECT) {
        		lastRound.setPoints(0);
        	} else if (v == Validation.CHEATED) {
        		lastRound.setPoints(-1);
        	}
        	game.getRounds().add(lastRound);
            roundService.saveRound(lastRound);
            LOGGER.info("Round nr '{}', with team '{}' was saved, gamerounds '{}', points '{}'", lastRound.getNr(),
                    lastRound.getGuessingTeam().getName(), game.getRounds().size(), lastRound.getPoints());
        }
    }
}
