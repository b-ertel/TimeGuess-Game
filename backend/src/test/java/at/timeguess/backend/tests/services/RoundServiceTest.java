package at.timeguess.backend.tests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.RoundService;

/**
 * Some very basic tests for {@link GameService}.
 */
@SpringBootTest
@WebAppConfiguration
public class RoundServiceTest {

    @Autowired
    RoundService roundService;
    
    @Autowired
    GameRepository gameRepo;

    @Test
    public void getAllRounds() {
    	Assertions.assertEquals(19, roundService.getAllRounds().size());
    }
    
    @DirtiesContext
    @Test
    public void testRoundsPlayedinGame() {
    	Game gameWithRounds = gameRepo.findById((long) 1).get();
    	Assertions.assertTrue(roundService.roundsPlayedInGame(gameWithRounds));
    	
    	Game gameWithoutRounds = gameRepo.findById((long) 6).get();
    	Assertions.assertFalse(roundService.roundsPlayedInGame(gameWithoutRounds));
    }
    
    @DirtiesContext
    @Test
    public void testRoundsPlayedinGameWithTeam() {
    	Game gameWithRounds = gameRepo.findById((long) 1).get();
    	Team teamWithRounds = gameWithRounds.getTeams().iterator().next().getTeam();
    	Assertions.assertTrue(roundService.teamPlayedRoundsInGame(gameWithRounds, teamWithRounds));
    	
    	Game gameWithoutRounds = gameRepo.findById((long) 6).get();
    	Assertions.assertFalse(roundService.teamPlayedRoundsInGame(gameWithoutRounds, teamWithRounds));
    	
    	Team teamWithNoRoundsInGame = gameRepo.findById((long) 2).get().getTeams().iterator().next().getTeam();
    	Assertions.assertFalse(roundService.teamPlayedRoundsInGame(gameWithoutRounds, teamWithNoRoundsInGame));
    }
    
    @DirtiesContext
    @Test
    public void testGetLastRound() {
    	for(int i=1; i<5; i++) {
    		Game game = gameRepo.findById((long) i).get();
        	Round lastRound = roundService.getLastRound(game);
        	Assertions.assertEquals(lastRound.getId(), (long) 4*i);
    	}
    	Game gameWithNoRound = gameRepo.findById((long) 6).get();
    	Assertions.assertNull(roundService.getLastRound(gameWithNoRound));
    }  
    
    
    @DirtiesContext
    @Test
    public void testGetLastRoundOfTeam() {
    	Game gameWithRounds = gameRepo.findById((long) 1).get();
    	Team teamWithRounds = gameWithRounds.getTeams().iterator().next().getTeam();
    	Assertions.assertEquals(teamWithRounds.getId(),roundService.getLastRoundOfTeam(gameWithRounds, teamWithRounds).getGuessingTeam().getId());
    	
    	Game gameWithoutRounds = gameRepo.findById((long) 6).get();
    	Assertions.assertNull(roundService.getLastRoundOfTeam(gameWithoutRounds, teamWithRounds));
    } 
    
    
}
