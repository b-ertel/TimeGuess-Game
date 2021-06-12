package at.timeguess.backend.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.repositories.TeamRepository;
import at.timeguess.backend.repositories.TermRepository;
import at.timeguess.backend.repositories.TopicRepository;
import at.timeguess.backend.repositories.UserRepository;

/**
 * Tests for {@link GameLogicService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class TopicStatisticServiceTest {

    @Autowired
    TopicStatisticService statService;

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
    
    @Autowired
    UserRepository userRepo;

    @DirtiesContext
    @Test
    public void testNrOfGamesPerTopic() {
    	Topic geography = topicRepo.findById((long) 1).get();
    	Topic movies = topicRepo.findById((long) 2).get();
    	
    	Assertions.assertEquals(2, statService.nrOfGamesWithTopic(geography));
    	Assertions.assertEquals(2, statService.nrOfGamesWithTopic(movies));
    }  
    
    @DirtiesContext
    @Test
    public void testMostUsedTopic() {
    	Assertions.assertEquals(topicRepo.findById((long) 3).get(), statService.getMostUsedTopic());
    }
    
    @DirtiesContext
    @Test
    public void testLeastUsedTopic() {
    	Assertions.assertEquals(topicRepo.findById((long) 4).get(), statService.getLeastUsedTopic());
    }
    
    @DirtiesContext
    @Test
    public void testNrOfCorrectRounds() {
    	Assertions.assertEquals(3, statService.nrOfCorrectRounds(topicRepo.findById((long)1).get()));
    	Assertions.assertEquals(7, statService.nrOfCorrectRounds(topicRepo.findById((long)2).get()));
    }
    
    @DirtiesContext
    @Test
    public void testNrOfIncorrectRounds() {
    	Assertions.assertEquals(1, statService.nrOfInCorrectRounds(topicRepo.findById((long)1).get()));
    	Assertions.assertEquals(1, statService.nrOfInCorrectRounds(topicRepo.findById((long)2).get()));
    }
    
    @DirtiesContext
    @Test
    public void testNrOfTopics() {
    	Assertions.assertEquals(4, statService.nrOfTopics());
    }
    
    @DirtiesContext
    @Test
    public void testNrOfTerms() {
    	Assertions.assertEquals(20, statService.nrOfTerms());
    }
    
    @DirtiesContext
    @Test
    public void testNrOfPlayers() {
    	Assertions.assertEquals(9, statService.nrOfPlayers());
    }
    
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER", "PLAYER" })
    @Test
    public void testGetMostSuccessfullUser() {
    	Topic geography = topicRepo.findById((long) 1).get();
    	User verena = userRepo.findFirstByUsername("verena");
    	
    	Assertions.assertEquals(verena, statService.getMostSuccessfullUserOfTopic(geography));
    }
}


