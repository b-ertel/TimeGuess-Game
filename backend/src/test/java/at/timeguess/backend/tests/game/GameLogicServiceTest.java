package at.timeguess.backend.tests.game;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
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
    GameRepository gameRepo;

    @Autowired
    GameLogicService gameLogic;

    
    @DirtiesContext
    @Test
    public void randomOrderValidation() {
        long id = 1;
        long id2 = 2;
        Set<GameTeam> teams = new HashSet<>();
        teams.addAll(gameRepo.findById(id).get().getTeams());
        teams.addAll(gameRepo.findById(id2).get().getTeams());
        Map<Integer, Team> result = gameLogic.getRandomTeamOrder(teams);
        Assertions.assertEquals(4, result.size());
        for(int i = 1; i<5; i++) {
        	Assertions.assertTrue(result.containsKey(i));
        }
        Assertions.assertFalse(result.containsKey(0));
        Assertions.assertFalse(result.containsKey(5));
        Assertions.assertNotNull(result.get(1));
        for(int i=1; i<4; i++) {
        	for(int j=i+1; j<5; j++) {
        		Assertions.assertNotEquals(result.get(i), result.get(j));
        	}
        }
        
    }

   
}
