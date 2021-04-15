package at.timeguess.backend.tests.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.GameTeam;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.services.UserService;

/**
 * Some very basic tests for {@link GameService}.
 */
@SpringBootTest
@WebAppConfiguration
public class GameLogicServiceTest {

    @Autowired
    GameLogicService gameLogicService;

    @Autowired
    GameService gameService;


    @DirtiesContext
    @Test
    public void getNextTeamRightForGameWithRounds() {
    	for(int i = 1; i<6; i++) {
    		long id = (long) i;
    		Game game = gameService.loadGame(id);
            Set<GameTeam> gteam = game.getTeams();
            Team result = gteam.iterator().next().getTeam();
            Assertions.assertEquals(result, gameLogicService.getNextTeam(game));
    	}    
    }
    
    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void getNextTeamRightForNewGame() {
    	Game game = new Game();
    	game.setTeams(gameService.loadGame((long) 5).getTeams());
    	gameService.saveGame(game);
    	List<Team> teams = new ArrayList<>();
    	Iterator<GameTeam> ite = game.getTeams().iterator();
    	while(ite.hasNext()) {
    		teams.add(ite.next().getTeam());
    	}
    	Assertions.assertTrue(teams.contains(gameLogicService.getNextTeam(game)));
    	
    }
}
