package at.timeguess.backend.ui.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.exceptions.AllTermsUsedInGameException;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.ui.beans.NewGameBean;
import at.timeguess.backend.ui.beans.NewTeamBean;
import at.timeguess.backend.ui.beans.NewUserBean;

@Component
@Scope("view")
public class StartRoundController {

    @Autowired
    private GameLogicService gameLogic;

    private Game game;
    private Set<Team> teams;
    private String message;

    public void setGame(Game game) {
        this.game = game;
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

    // TODO: REMOVE! only here for test purpose (to create new game and teams)
    // DEMO CREATING NEW GAME FOR 2 TEAMS WITH 2 NEW USERS EACH ON-THE-FLY
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private NewGameBean newGameBean;
    @Autowired
    private NewTeamBean newTeamBean;
    @Autowired
    private NewUserBean newUserBean;

    private static int newGameCounter = 1;
    private static int newTeamCounter = 1;
    private static int newUserCounter = 1;

    public void createGame() {
        newGameBean.clearFields();
        newGameBean.setGameName("TestGame " + newGameCounter++);
        newGameBean.setMaxPoints(10);
        newGameBean.setTopic(topicRepository.findById(4L).get());
        newGameBean.addNewTeam(createTeam());
        newGameBean.addNewTeam(createTeam());

        game = newGameBean.createGame();
        teams = game.getTeams();
        game.setRoundNr(0);
    }

    private Team createTeam() {
        newTeamBean.clearFields();
        newTeamBean.setTeamName("TestTeam " + newTeamCounter++);
        newTeamBean.setPlayers(Set.of(createUser(), createUser()));
        return newTeamBean.createTeam();
    }

    private User createUser() {
        newUserBean.clearFields();
        newUserBean.setUsername("TestUser " + newUserCounter++);
        newUserBean.setPassword("pw");
        newUserBean.setRepeated("pw");
        return newUserBean.createUser();
    }
}
