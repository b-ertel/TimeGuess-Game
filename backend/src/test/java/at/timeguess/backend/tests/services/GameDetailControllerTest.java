package at.timeguess.backend.tests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.controllers.GameDetailController;

@SpringBootTest
@WebAppConfiguration
public class GameDetailControllerTest {
    
    @Autowired
    private GameDetailController gameDetailController;

    @Autowired
    private GameService gameService;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testPrimitiveSettingGame() {
        Game g1 = gameService.loadGame(1L);
        Game g2 = gameService.loadGame(2L);

        // gameDetailController.setGame(null);
        Assertions.assertNull(gameDetailController.getGame(), "GameDetailController holds a game");
        gameDetailController.setGame(g1);
        Assertions.assertNotNull(gameDetailController.getGame(), "GameDetailController doesn't hold a game");
        Assertions.assertEquals(gameDetailController.getGame(), g1, "Not game 1 in controller");
        gameDetailController.setGame(g2);
        Assertions.assertNotNull(gameDetailController.getGame(), "GameDetailController doesn't hold a game");
        Assertions.assertEquals(gameDetailController.getGame(), g2, "Not game 2 in controller");
    }
}
