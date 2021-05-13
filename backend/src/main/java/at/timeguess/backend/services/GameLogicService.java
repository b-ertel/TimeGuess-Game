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
        List<Term> availableTerms = termService.getAllEnabledTermsOfTopic(game.getTopic());
        Set<Term> usedTerms = usedTerms(game);
        return !(availableTerms.size() == usedTerms.size());
    }
    
    /**
     * method to check wheter a team have reached the max points of a game
     * @param game with a maximum of points
     * @param team to check
     * @return boolean
     */
    public boolean teamReachedMaxPoints(Game game, Team team) {
    	if(game.getMaxPoints()<=roundService.getPointsOfTeamInGame(game, team)) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * method to evaluate the team with the most points in a game
     * @param game to evaluate
     * @return team with the most points
     */
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
        Term next = terms.get(Math.abs(rand.nextInt(terms.size())));
        while (!next.isEnabled()) {
            Random newRand = new Random();
            next = terms.get(Math.abs(newRand.nextInt(terms.size())));
        }
        return next;
    }

    /**
     * Method to estimate which team comes next. If no rounds played in game, a  random team
     * is chosen. If a round is played, the next team in the list is returned
     * @param game to evaluate next team
     * @return team that plays next
     */
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

    /**
     * Method to estimate which user comes next. If team of user has not played any rounds in game, a random user
     * is chosen. If a round is played by the team , the next user in the list is returned
     * @param game to evaluate next team
     * @param team of users
     * @return user that plays next
     */
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

    
    /**
     * Method to create next round of a game. Sets all values of {@link Round} except for points, time and activity
     * @param game to evaluate
     * @return next round
     */
    public Round getNextRound(Game game) {
		Round nextRound = new Round();
		nextRound.setNr(game.getRounds().size()+1);
		Team nextTeam = getNextTeam(game);
		nextRound.setGuessingUser(nextUser(game, nextTeam));
		nextRound.setGuessingTeam(nextTeam);
		Set<Team> verifiyingTeams = game.getTeams();
		verifiyingTeams.remove(nextTeam);
		nextRound.setVerifyingTeams(verifiyingTeams);
		nextRound.setTermToGuess(nextTerm(game));
		nextRound.setGame(game);
		game.getRounds().add(nextRound);
		game.setRoundNr(game.getRoundNr()+1);
		LOGGER.info("New Round nr '{}', with team '{}' and user '{}' was created", nextRound.getNr(), nextRound.getGuessingTeam().getName(), nextRound.getGuessingUser().getUsername());
		return nextRound;
	}
    
    /**
     * Method that sets informations of cube-face into round
     * @param round that gets informations
     * @param cubeFace that delivers informations
     * @return updated round
     */
    public Round getCubeInfosIntoRound(Round round, CubeFace cubeFace) {
    	round.setActivity(cubeFace.getActivity());
    	round.setPoints(cubeFace.getPoints());
    	round.setTime(cubeFace.getTime());
    	return round;
    }
    
    /**
     * Saves last round of game with corresponding validation
     * @param game of which last round should be saved
     * @param v validation of last round
     */
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
