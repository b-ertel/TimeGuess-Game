package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.TermRepository;

@Component
@Scope("application")
public class GameLogicService {

	@Autowired
	TermService termService;
	
	@Autowired
	RoundRepository roundRepo;
	
	@Autowired
	RoundService roundService;
	
	/**
	 * A method that generates a random order of the teams. It generates to every team in the set a random integer, that represents the place of the team.
	 * @param teams: the GameTeams that should be ordered
	 * @return a map with the place of the team as key and the team as value
	 */
	public Team getNextTeam(Game game){
		List<Team> teams = new ArrayList<>();
		Set<GameTeam> gteams = game.getTeams();
		Iterator<GameTeam> ite = gteams.iterator();
		while(ite.hasNext()) {
			teams.add(ite.next().getTeam());
		}
		if(roundRepo.getTeamOfLastRound(game).size()!=0) {
			Team teamOfLastRound = roundRepo.getTeamOfLastRound(game).get(0).getGuessingTeam();
			if(teams.indexOf(teamOfLastRound)==(teams.size()-1)) {
				return teams.get(0);
			} else {
				return teams.get(teams.indexOf(teamOfLastRound)+1);
			}
		} else {
			Random rand = new Random();
			return teams.get(rand.nextInt(teams.size()));
		}
	}
	
	/**
	 * method to check wether all terms of a topic have been used or not
	 * @param game
	 * @return boolean wether there a still terms available or not
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
	public Term nextTerm(Game game) /*throws AllTermsUsedInGameException*/ {
		/*if (stillTermsAvailable(game)) {*/
			List<Term> terms = termService.getAllTermsOfTopic(game.getTopic());
			Set<Term> usedTerms = usedTerms(game);
			usedTerms.stream().forEach(term -> terms.remove(term));
			Random rand = new Random();
			return terms.get(rand.nextInt(terms.size()));
		/*} else {
			throw new AllTermsUsedInGameException();
		}*/
	}
	
	public User nextUser(Game game) {
		Team team = getNextTeam(game);
		return team.getTeamMembers().iterator().next();
	}
	
	
	public void startNewRound(Game game) {
		Round nextRound = new Round();
		nextRound.setNr(game.getRoundNr()+1);
		nextRound.setGuessingUser(nextUser(game));
		nextRound.setGuessingTeam(getNextTeam(game));
		nextRound.setTermToGuess(nextTerm(game));
		nextRound.setGame(game);
		game.getRounds().add(nextRound);
		game.setRoundNr(game.getRoundNr()+1);
	}
	
	
	public void saveLastRound(Game game) {
		Set<Round> rounds = game.getRounds();
		Round lastRound = null;
		Iterator<Round> ite = rounds.iterator();
		while(ite.hasNext()) {
			lastRound = ite.next();
		}
		if(lastRound != null) {
			roundService.saveRound(lastRound);
		}
	}
}
