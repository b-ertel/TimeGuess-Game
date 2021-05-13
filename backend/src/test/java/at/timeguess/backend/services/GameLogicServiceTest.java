package at.timeguess.backend.services;

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
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Term;
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
}
