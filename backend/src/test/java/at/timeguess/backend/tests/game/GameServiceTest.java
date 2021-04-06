package at.timeguess.backend.tests.game;

import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TopicService;
import at.timeguess.backend.services.UserService;

/**
 * Some very basic tests for {@link UserService}.
 *
 * This class is part of the skeleton project provided for students of the
 * courses "Software Architecture" and "Software Engineering" offered by the
 * University of Innsbruck.
 */
@SpringBootTest
@WebAppConfiguration
public class GameServiceTest {

    @Autowired
    GameService gameService;

    @Autowired
    TopicService topicService;

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    public void testDatainitialization() {
        Assertions.assertTrue(gameService.getAllGames().size() >= 5, "Insufficient amount of games initialized for test data source");
        for (Game game : gameService.getAllGames()) {
            if ("Game 1".equals(game.getName())) {
                Assertions.assertTrue(game.getMaxPoints() == 40, "Game \"" + game + "\" does not have 45 maxPoints (it has" + game.getMaxPoints() + ").");
                Assertions.assertTrue(game.getStatus() == GameState.FINISHED, "Game \"" + game + "\" is not finished");
                Assertions.assertTrue(game.getTopic().getId() == 1, "Game \"" + game + "\" has wrong topic");
            }  
        }
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN", "MANAGER"})
    public void canFindGame() {
        long id = 1;
        Game game = gameService.loadGame(id);
        Assertions.assertNotNull(game, "Game \"" + id + "\" could not be loaded from test data source");
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN", "MANAGER"})
    public void testDeleteGame() {
        long id_to_del = 3;
        int ctBefore = gameService.getAllGames().size();
        Game gameToDelete = gameService.loadGame(id_to_del);
        Assertions.assertNotNull(gameToDelete, "Game \"" + id_to_del + "\" could not be loaded from test data source");

        gameService.deleteGame(gameToDelete);

        Assertions.assertEquals(ctBefore - 1, gameService.getAllGames().size(), "No game has been deleted after calling gameService.deleteGame");
        Assertions.assertThrows(NoSuchElementException.class, () ->  gameService.loadGame(id_to_del));
    }

    @DirtiesContext
    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN", "MANAGER"})
    public void testUpdateGame() {
        long id = 4;
        Game game = gameService.loadGame(id);
        Assertions.assertNotNull(game, "Game \"" + id + "\" could not be loaded from test data source");

        // Simple properties
        String origName = game.getName();
        Assertions.assertEquals(game.getName(), "Game 4", "Name not equal 'Game 4'");
        String newName = "My fantastic game";
        game.setName(newName);

        int origPoints = game.getMaxPoints();
        int newPoints = 100;
        game.setMaxPoints(newPoints);  

        int origRound = game.getRoundNr();
        int newRound = 10;
        game.setRoundNr(newRound);

        // NOTE will need update in future if setStatus implements confirmation logic
        GameState oldStatus = game.getStatus();
        GameState newStatus = GameState.VALID_SETUP;
        game.setStatus(newStatus);

        // complex properties
        Topic oldTopic = game.getTopic();
        Topic newTopic = topicService.loadTopicId(1L);

        game.setTopic(newTopic);


        // TODO Team and Round need work,
        // probably require services to be setup
        //
        // currently errors:
        // org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: at.timeguess.backend.model.Game.teams, could not initialize proxy - no Session
        //  at at.timeguess.backend.tests.game.GameServiceTest.testUpdateGame(GameServiceTest.java:113)
        //
        //Set<Team> oldTeams = game.getTeams();
        //int oldTeamLen = oldTeams.size();
        //Assertions.assertTrue(oldTeamLen > 0, "Needs one team");

        gameService.saveGame(game);


        
        Game saveGame = gameService.loadGame(id);
        Assertions.assertNotNull(saveGame, "Game \"" + id + "\" could not be reloaded from test data source");

        Assertions.assertNotEquals(saveGame.getName(), origName, "Games still has original name");
        Assertions.assertEquals(saveGame.getName(), newName, "Games name was not updated");

        Assertions.assertNotEquals(saveGame.getMaxPoints(), origPoints, "Games still has original maxPoints");
        Assertions.assertEquals(saveGame.getMaxPoints(), newPoints, "Games maxPoints was not updated");

        Assertions.assertNotEquals(saveGame.getRoundNr(), origRound, "Games still has original RoundNr");
        Assertions.assertEquals(saveGame.getRoundNr(), newRound, "Games RoundNr was not updated");

        Assertions.assertNotEquals(saveGame.getStatus(), oldStatus, "Games still has original Status");
        Assertions.assertEquals(saveGame.getStatus(), newStatus, "Games Status was not updated");

        Assertions.assertNotEquals(saveGame.getTopic(), oldTopic, "Games still has original Topic");
        Assertions.assertEquals(saveGame.getTopic(), newTopic, "Games Topic was not updated");

    }

}
