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

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.User;
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
	private boolean stillTermsAvailable(Game game) {
		if(usedTerms(game).size() == termService.getAllTermsOfTopic(game.getTopic()).size())
				return false;
		else
			return true;
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
	public Term nextTerm(Game game) throws AllTermsUsedInGameException {
		if (stillTermsAvailable(game)) {
			List<Term> terms = termService.getAllTermsOfTopic(game.getTopic());
			Set<Term> usedTerms = usedTerms(game);
			usedTerms.stream().forEach(term -> terms.remove(term));
			Random rand = new Random();
			return terms.get(rand.nextInt(terms.size()));
		} else {
			throw new AllTermsUsedInGameException();
		}
	}
	
	public Team getNextTeam(Game game){
		List<Team> teams = new ArrayList<>();
		Set<GameTeam> gteams = game.getTeams();
		Iterator<GameTeam> ite = gteams.iterator();
		while(ite.hasNext()) {
			teams.add(ite.next().getTeam());
		}
		if(roundService.roundsPlayedInGame(game)) {
			Team teamOfLastRound = roundService.getLastRound(game).getGuessingTeam();
			int index = 0;
			for(Team t: teams) {
				if (t.getName() == teamOfLastRound.getName()) {
					index = teams.indexOf(t);
				}
			}
			if(index == (teams.size()-1)) {
				return teams.get(0);
			} else {
				return teams.get(index+1);
			}
		} else {
			Random rand = new Random();
			return teams.get(rand.nextInt(teams.size()));
		}
	}
	
	
	public User nextUser(Game game, Team team) {
		List<User> users = new ArrayList<>();
		Iterator<User> ite = team.getTeamMembers().iterator();
		while(ite.hasNext()) {
			users.add(ite.next());
		}
		if(roundService.teamPlayedRoundsInGame(game, team)) {
			User lastUser = roundService.getLastRoundOfTeam(game, team).getGuessingUser();
			int index = 0;
			for(User u: users) {
				if (u.getUsername() == lastUser.getUsername()) {
					index = users.indexOf(u);
				}
			}
			if(index == (users.size()-1)) {
				return users.get(0);
			} else {
				return users.get(index+1);
			}
		} else {
			Random rand = new Random();
			return users.get(rand.nextInt(users.size()));
		}
	}
	
	
	public Game startNewRound(Game game) throws AllTermsUsedInGameException {
		Round nextRound = new Round();
		nextRound.setNr(game.getRounds().size()+1);
		Team nextTeam = getNextTeam(game);
		nextRound.setGuessingUser(nextUser(game, nextTeam));
		nextRound.setGuessingTeam(nextTeam);
		nextRound.setTermToGuess(nextTerm(game));
		nextRound.setGame(game);
		game.getRounds().add(nextRound);
		game.setRoundNr(game.getRounds().size());
		
		LOGGER.info("New Round nr '{}', with team '{}' and user '{}' was created", nextRound.getNr(), nextRound.getGuessingTeam().getName(), nextRound.getGuessingUser().getUsername());
		return game;
	}
	
	
	public void saveLastRound(Game game) {
		Set<Round> rounds = game.getRounds();
		Round lastRound = null;
		Iterator<Round> ite = rounds.iterator();
		int highestNr = 0;
		while(ite.hasNext()) {
			Round aRound = ite.next();
			if(aRound.getNr() > highestNr) {
				highestNr = aRound.getNr();
				lastRound = aRound;
			}
		}
		if(lastRound != null) {
			roundService.saveRound(lastRound);
			LOGGER.info("Round nr '{}', with team '{}' was saved, gamerounds '{}'", lastRound.getNr(), lastRound.getGuessingTeam().getName(), game.getRounds().size());
		}
	}
}