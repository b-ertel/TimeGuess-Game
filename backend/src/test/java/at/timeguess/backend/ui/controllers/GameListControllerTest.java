package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link GameListController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameListControllerTest {

    @InjectMocks
    private GameListController gameListController;

    @Mock
    private GameService gameService;

    private static List<Game> expected;

    @BeforeAll
    private static void setup() {
        expected = createEntities(TestSetup::createGame, 10);
    }

    @Test
    public void testIsAdmin() {
        when(gameService.getAllGames()).thenReturn(expected);

        gameListController.setAdmin(true);
        List<Game> result = gameListController.getGames();

        verify(gameService).getAllGames();
        assertEquals(expected, result);
    }

    @Test
    public void testIsNotAdmin() {
        when(gameService.getAllCurrent()).thenReturn(expected);

        gameListController.setAdmin(false);
        List<Game> result = gameListController.getGames();

        verify(gameService).getAllCurrent();
        assertEquals(expected, result);

        gameListController.setAdmin(null);
        result = gameListController.getGames();

        verify(gameService, times(2)).getAllCurrent();
        assertEquals(expected, result);
    }

    @Test
    public void testGetGames() {
        User user = createUser(15L);
        when(gameService.getByUser(user, false)).thenReturn(expected);

        List<Game> result = gameListController.getGames(user);

        verify(gameService).getByUser(user, false);
        assertEquals(expected, result);
    }

    @Test
    public void testGetGamesCurrent() {
        User user = createUser(15L);
        when(gameService.getByUser(user, true)).thenReturn(expected);

        List<Game> result = gameListController.getGamesCurrent(user);

        verify(gameService).getByUser(user, true);
        assertEquals(expected, result);
    }

    @Test
    public void testFilterGames() {
        when(gameService.getAllGames()).thenReturn(expected);

        // test setting filter to null
        gameListController.setAdmin(true);
        gameListController.setFilterGames(null);
        Collection<Game> result = gameListController.getFilterGames();

        verify(gameService).getAllGames();
        assertEquals(expected, result);

        // test setting filter to list
        reset(gameService);
        gameListController.setFilterGames(expected);
        result = gameListController.getFilterGames();

        verifyNoInteractions(gameService);
        assertTrue(expected == result);
    }

    @Test
    public void testSelectedGames() {
        Game game = expected.get(5);
        Game result = gameListController.getSelectedGame();
        assertNotEquals(game, result);

        gameListController.setSelectedGame(null);
        assertNull(gameListController.getSelectedGame());

        gameListController.setSelectedGame(game);
        assertTrue(game == gameListController.getSelectedGame());
    }

    @Test
    public void testIsDisabledConfirmation() {
        User user = createUser(25L);
        Game game = expected.get(7);
        when(gameService.disabledConfirmation(user, game)).thenReturn(true);

        boolean result = gameListController.isDisabledConfirmation(user, game);

        verify(gameService).disabledConfirmation(user, game);
        assertTrue(result);

        when(gameService.disabledConfirmation(user, game)).thenReturn(false);
        result = gameListController.isDisabledConfirmation(user, game);

        verify(gameService, times(2)).disabledConfirmation(user, game);
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsLockedDelete(Long gameId) {
        Game game = expected.get(4);

        game.setStatus(GameState.SETUP);
        assertFalse(gameListController.isLockedDelete(game));
        game.setStatus(GameState.VALID_SETUP);
        assertFalse(gameListController.isLockedDelete(game));
        game.setStatus(GameState.HALTED);
        assertFalse(gameListController.isLockedDelete(game));
        game.setStatus(GameState.FINISHED);
        assertFalse(gameListController.isLockedDelete(game));
        game.setStatus(GameState.CANCELED);
        assertFalse(gameListController.isLockedDelete(game));

        game.setStatus(GameState.PLAYED);
        assertTrue(gameListController.isLockedDelete(game));
        game = null;
        assertTrue(gameListController.isLockedDelete(game));        

        verifyNoInteractions(gameService);
    }

    @Test
    public void testDoConfirma() {
        User user = createUser(19L);
        Game game = expected.get(3);

        gameListController.doConfirm(user, game);

        verify(gameService).confirm(user, game);
    }
}
