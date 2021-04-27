package at.timeguess.backend.ui.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
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

    Set<Team> teams;

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
        }
        catch (AllTermsUsedInGameException e) {
            this.message = "All Terms of topic have been used, game ends";
        }
    }

    public int numberRounds() {
        return game.getRounds().size();
    }

    public boolean hasNoRounds() {
        if (game == null) return true;
        else return game.getRounds().isEmpty();
    }

    public void saveLastRound() {
        gameLogic.saveLastRound(game);
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
