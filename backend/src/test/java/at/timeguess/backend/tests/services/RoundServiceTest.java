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
import at.timeguess.backend.model.Term;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.ui.beans.NewGameBean;

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
    public void getLastRoundTest() {
    	for(int i=1; i<5; i++) {
    		Game game = gameRepo.findById((long) i).get();
        	Round lastRound = roundService.getLastRound(game);
        	Assertions.assertEquals(lastRound.getId(), (long) 4*i);
    	}
    	Game gameWithNoRound = gameRepo.findById((long) 6).get();
    	Assertions.assertNull(roundService.getLastRound(gameWithNoRound));
    }  
}
