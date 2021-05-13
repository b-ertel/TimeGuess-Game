package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.PushContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.repositories.GameRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link GameService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameServiceMockedTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;
    @Mock
    private MessageBean messageBean;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testSaveGame() {
        Game game = createGame(5L);
        game.setName("aname");
        PushContext context = mock(PushContext.class);
        when(gameRepository.save(game)).thenReturn(game);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertThrows(NullPointerException.class, () -> gameService.saveGame(game));

        verify(gameRepository).save(game);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }

    @Test
    public void testDeleteGame() {
        Game game = createGame(5L);
        game.setName("aname");
        PushContext context = mock(PushContext.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> gameService.deleteGame(game));

        verify(gameRepository).delete(game);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }
}
