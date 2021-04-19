package at.timeguess.backend.ui.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.exceptions.AllTermsUsedInGameException;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.beans.NewGameBean;

@Component
@Scope("view")
public class StartRoundController {

	@Autowired
	NewGameBean newGameBean;
	
	@Autowired
	TopicRepository topicRepo;
	
	@Autowired
	GameService gameService;
	
	@Autowired
	GameLogicService gameLogic;
	
	Game game;
	
	Set<GameTeam> teams;
	
	String message;
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public void createGame() {
		newGameBean.setGameName("TestGame");
		newGameBean.setMaxPoints(10);
		newGameBean.setTopic(topicRepo.findById((long) 1).get());
		newGameBean.createGame();
		this.game = gameService.loadGame((long) 8);
		game.setTeams(gameService.loadGame((long) 1).getTeams());
		game.getTeams().addAll(gameService.loadGame((long) 2).getTeams());
		game.setRoundNr(0);
		this.teams = game.getTeams();
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public void nextRound() {
		try {
			this.game = gameLogic.startNewRound(game);
		} catch(AllTermsUsedInGameException e) {
			this.message = "All Terms of topic have been used, game ends";
		} 
	}
	
	public int numberRounds() {
		return game.getRounds().size();
	}
	
	public String lastRound() {
		Round lastRound = new Round();
		for(Round round : game.getRounds()) {
			lastRound = round;
		}
		if(lastRound.getGuessingUser()==null) {
			return "No rounds played";
		} else {
			String str="";
			str += "Roundnumber: " + lastRound.getNr();
			str += " || Team: " + lastRound.getGuessingTeam().getName();
			str += " || User: " + lastRound.getGuessingUser().getUsername();
			str += " || Term: " + lastRound.getTermToGuess().getName();
			return str;
		}
		
	}
	
	public boolean hasNoRounds() {
		if(game==null)
			return true;
		else
			return game.getRounds().isEmpty();
	}
	
	public void saveLastRound() {
		gameLogic.saveLastRound(game);
	}

	public Set<GameTeam> getTeams() {
		return teams;
	}

	public void setTeams(Set<GameTeam> teams) {
		this.teams = teams;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
}
