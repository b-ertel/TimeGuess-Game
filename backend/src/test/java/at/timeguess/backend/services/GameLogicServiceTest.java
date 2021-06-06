package at.timeguess.backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.TeamRepository;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.repositories.TopicRepository;

/**
 * Tests for {@link GameLogicService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class GameLogicServiceTest {

    @Autowired
    GameLogicService gameLogicService;

    @Autowired
    GameService gameService;

    @Autowired
    TopicRepository topicRepo;

    @Autowired
    TermRepository termRepo;
    
    @Autowired
    TeamRepository teamRepo;
    
    @Autowired
    RoundRepository roundRepo;

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testNextTeam() {
        Game game = new Game();

        game.setName("TestGame");
        game.setMaxPoints(10);
        game.setTopic(topicRepo.findById((long) 1).get());
        game.setStatus(GameState.SETUP);
        gameService.saveGame(game);
        game.setTeams(gameService.loadGame((long) 1).getTeams());

        Set<Team> teamlist = game.getTeams();
        Assertions.assertTrue(teamlist.contains(gameLogicService.getNextTeam(game)));

        // Check if method chooses startteam randomly or if its the same every time
        Team currentTeam = null;
        int nrDifferentStartTeams = 0;
        for (int i = 0; i < 10; i++) {
            Team nextTeam = gameLogicService.getNextTeam(game);
            if (nextTeam != currentTeam) {
                nrDifferentStartTeams++;
                currentTeam = nextTeam;
            }
        }
        Assertions.assertTrue(nrDifferentStartTeams > 1);

        // TODO: check for existing game with rounds
    }

    @DirtiesContext
    @Test
    public void testUsedTerms() {
        Game game = gameService.loadGame((long) 1);
        Term termNotUsed = termRepo.findById((long) 5).get();
        Assertions.assertFalse(gameLogicService.usedTerms(game).contains(termNotUsed));

        for (int i = 1; i <= 4; i++) {
            Term termUsed = termRepo.findById((long) i).get();
            Assertions.assertTrue(gameLogicService.usedTerms(game).contains(termUsed));
        }
    }
    
    @DirtiesContext
    @Test
    public void teststillTermsAvailable() {
    	Game game = gameService.loadGame((long) 1);
    	Assertions.assertTrue(!gameLogicService.stillTermsAvailable(game));
    	Game game2 = gameService.loadGame((long) 2);
    	Assertions.assertTrue(gameLogicService.stillTermsAvailable(game2));
    }
    
    @DirtiesContext
    @Test
    public void testGetTeamWithMostPoints() {
    	Game game = gameService.loadGame((long) 1);
    	Team teamWithMostPoints = teamRepo.findById((long) 2).get();
    	Assertions.assertEquals(teamWithMostPoints, gameLogicService.getTeamWithMostPoints(game));  	
    }
    
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    @Test
    public void testSaveGame() {
    	Game game = new Game();
    	game.setName("TestGame");
        game.setMaxPoints(10);
        game.setTopic(topicRepo.findById((long) 1).get());
        game.setStatus(GameState.PLAYED);
        gameService.saveGame(game);
        game.setTeams(gameService.loadGame((long) 1).getTeams());
        gameLogicService.getNextRound(game);
        gameLogicService.saveLastRound(game, Validation.CHEATED);
        Assertions.assertEquals(20, roundRepo.findAll().size());
    }
    
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    @Test
    public void testPlayRound() {
    	Game game = new Game();
        game.setName("TestGame");
        game.setMaxPoints(10);
        game.setTopic(topicRepo.findById((long) 1).get());
        game.setStatus(GameState.PLAYED);
        gameService.saveGame(game);
        game.setTeams(gameService.loadGame((long) 1).getTeams());
        
        Round lastRound = null;
        List<Term> usedTerms = new ArrayList<>();
        int i = 1;
        while (true) {
        	Round newRound = gameLogicService.getNextRound(game);
        	if(i!=1) {
	        	Assertions.assertNotEquals(newRound.getGuessingTeam(), lastRound.getGuessingTeam());
	        	Assertions.assertNotEquals(newRound.getGuessingUser(), lastRound.getGuessingUser());
	        	Assertions.assertNotEquals(newRound.getTermToGuess(), lastRound.getTermToGuess());
        	}
        	gameLogicService.saveLastRound(game, Validation.CORRECT);
        	Assertions.assertEquals(i, newRound.getNr());
        	usedTerms.add(newRound.getTermToGuess());
        	Assertions.assertFalse(usedTerms.contains(gameLogicService.nextTerm(game)));
        	lastRound = newRound;
        	i++;
        	if(i==3) {
        		break;
        	}
        }
    }
    
}


