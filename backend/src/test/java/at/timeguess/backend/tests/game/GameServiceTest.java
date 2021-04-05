package at.timeguess.backend.tests.game;

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
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.GameService;
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
    public void testDeleteGame() {
        long id_to_del = 3;
        int ctBefore = gameService.getAllGames().size();
        Game gameToDelete = gameService.loadGame(id_to_del);
        Assertions.assertNotNull(gameToDelete, "Game \"" + id_to_del + "\" could not be loaded from test data source");

        gameService.deleteGame(gameToDelete);

        Assertions.assertEquals(ctBefore - 1, gameService.getAllGames().size(), "No game has been deleted after calling gameService.deleteGame");
        Game deletedGame = gameService.loadGame(id_to_del);
        Assertions.assertNull(deletedGame, "Deleted Game \"" + id_to_del + "\" could still be loaded from test data source via gameService.loadGame");

    }

}
