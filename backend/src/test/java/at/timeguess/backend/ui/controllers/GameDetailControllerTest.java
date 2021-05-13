package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link GameDetailController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameDetailControllerTest {

    @InjectMocks
    private GameDetailController gameDetailController;

    @Mock
    private GameService gameService;
    @Mock
    private TeamService teamService;
    @Mock
    private CubeStatusController cubeStatusController;
    @Mock
    private MessageBean messageBean;

    @Test
    public void testGetAllGameStates() {
        Set<GameState> result = gameDetailController.getAllGameStates();

        assertTrue(result.contains(GameState.SETUP));
        assertTrue(result.contains(GameState.VALID_SETUP));
        assertTrue(result.contains(GameState.PLAYED));
        assertTrue(result.contains(GameState.HALTED));
        assertTrue(result.contains(GameState.FINISHED));
        assertTrue(result.contains(GameState.CANCELED));
    }

    @Test
    public void testGetAllTeams() {
        List<Team> expected = createEntities(TestSetup::createTeam, 10);
        when(teamService.getAllTeams()).thenReturn(expected);

        List<Team> result = gameDetailController.getAllTeams();

        verify(teamService).getAllTeams();
        assertEquals(expected, result);
    }

    @Test
    public void testGetAvailableTeams() {
        List<Team> expected = createEntities(TestSetup::createTeam, 10);
        when(teamService.getAvailableTeams()).thenReturn(expected);

        List<Team> result = gameDetailController.getAvailableTeams();

        verify(teamService).getAvailableTeams();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 14 })
    public void testCanTraverse(Long gameId) {
        assertMockGame(gameId);

        assertCanTraverse(GameState.SETUP, GameState.SETUP, true);
        assertCanTraverse(GameState.SETUP, GameState.VALID_SETUP, true);
        assertCanTraverse(GameState.SETUP, GameState.PLAYED, false);
        assertCanTraverse(GameState.SETUP, GameState.HALTED, false);
        assertCanTraverse(GameState.SETUP, GameState.FINISHED, false);
        assertCanTraverse(GameState.SETUP, GameState.CANCELED, true);

        assertCanTraverse(GameState.VALID_SETUP, GameState.SETUP, false);
        assertCanTraverse(GameState.VALID_SETUP, GameState.VALID_SETUP, true);
        assertCanTraverse(GameState.VALID_SETUP, GameState.PLAYED, true);
        assertCanTraverse(GameState.VALID_SETUP, GameState.HALTED, true);
        assertCanTraverse(GameState.VALID_SETUP, GameState.FINISHED, false);
        assertCanTraverse(GameState.VALID_SETUP, GameState.CANCELED, true);

        assertCanTraverse(GameState.PLAYED, GameState.SETUP, false);
        assertCanTraverse(GameState.PLAYED, GameState.VALID_SETUP, false);
        assertCanTraverse(GameState.PLAYED, GameState.PLAYED, true);
        assertCanTraverse(GameState.PLAYED, GameState.HALTED, true);
        assertCanTraverse(GameState.PLAYED, GameState.FINISHED, true);
        assertCanTraverse(GameState.PLAYED, GameState.CANCELED, true);

        assertCanTraverse(GameState.HALTED, GameState.SETUP, false);
        assertCanTraverse(GameState.HALTED, GameState.VALID_SETUP, false);
        assertCanTraverse(GameState.HALTED, GameState.PLAYED, true);
        assertCanTraverse(GameState.HALTED, GameState.HALTED, true);
        assertCanTraverse(GameState.HALTED, GameState.FINISHED, false);
        assertCanTraverse(GameState.HALTED, GameState.CANCELED, true);

        assertCanTraverse(GameState.FINISHED, GameState.SETUP, false);
        assertCanTraverse(GameState.FINISHED, GameState.VALID_SETUP, false);
        assertCanTraverse(GameState.FINISHED, GameState.PLAYED, false);
        assertCanTraverse(GameState.FINISHED, GameState.HALTED, false);
        assertCanTraverse(GameState.FINISHED, GameState.FINISHED, true);
        assertCanTraverse(GameState.FINISHED, GameState.CANCELED, true);

        assertCanTraverse(GameState.CANCELED, GameState.SETUP, false);
        assertCanTraverse(GameState.CANCELED, GameState.VALID_SETUP, false);
        assertCanTraverse(GameState.CANCELED, GameState.PLAYED, false);
        assertCanTraverse(GameState.CANCELED, GameState.HALTED, false);
        assertCanTraverse(GameState.CANCELED, GameState.FINISHED, false);
        assertCanTraverse(GameState.CANCELED, GameState.CANCELED, true);
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsAvailableTeam(Long gameId) {
        Game game = assertMockGame(gameId);
        Team team = createTeam(6L);

        when(teamService.isAvailableTeamForGame(team, game)).thenReturn(true);
        assertTrue(gameDetailController.isAvailableTeam(team));
        verify(teamService).isAvailableTeamForGame(team, game);
        assertEquals(gameId, gameDetailController.getGame().getId());

        reset(teamService);
        when(teamService.isAvailableTeamForGame(team, game)).thenReturn(false);
        assertFalse(gameDetailController.isAvailableTeam(team));
        verify(teamService).isAvailableTeamForGame(team, game);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsLockedMaxPoints(Long gameId) {
        Game game = assertMockGameAndReset(gameId);

        game.setStatus(GameState.SETUP);
        assertFalse(gameDetailController.isLockedMaxPoints());
        game.setStatus(GameState.VALID_SETUP);
        assertFalse(gameDetailController.isLockedMaxPoints());
        game.setStatus(GameState.HALTED);
        assertFalse(gameDetailController.isLockedMaxPoints());

        game.setStatus(GameState.PLAYED);
        assertTrue(gameDetailController.isLockedMaxPoints());
        game.setStatus(GameState.FINISHED);
        assertTrue(gameDetailController.isLockedMaxPoints());
        game.setStatus(GameState.CANCELED);
        assertTrue(gameDetailController.isLockedMaxPoints());

        verifyNoInteractions(gameService);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsLockedTopic(Long gameId) {
        Game game = assertMockGameAndReset(gameId);

        game.setStatus(GameState.SETUP);
        assertFalse(gameDetailController.isLockedTopic());
        game.setStatus(GameState.VALID_SETUP);
        assertFalse(gameDetailController.isLockedTopic());

        game.setStatus(GameState.PLAYED);
        assertTrue(gameDetailController.isLockedTopic());
        game.setStatus(GameState.HALTED);
        assertTrue(gameDetailController.isLockedTopic());
        game.setStatus(GameState.FINISHED);
        assertTrue(gameDetailController.isLockedTopic());
        game.setStatus(GameState.CANCELED);
        assertTrue(gameDetailController.isLockedTopic());

        verifyNoInteractions(gameService);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsLockedTeam(Long gameId) {
        Game game = assertMockGameAndReset(gameId);

        game.setStatus(GameState.SETUP);
        assertFalse(gameDetailController.isLockedTeam());
        game.setStatus(GameState.VALID_SETUP);
        assertFalse(gameDetailController.isLockedTeam());

        game.setStatus(GameState.PLAYED);
        assertTrue(gameDetailController.isLockedTeam());
        game.setStatus(GameState.HALTED);
        assertTrue(gameDetailController.isLockedTeam());
        game.setStatus(GameState.FINISHED);
        assertTrue(gameDetailController.isLockedTeam());
        game.setStatus(GameState.CANCELED);
        assertTrue(gameDetailController.isLockedTeam());

        verifyNoInteractions(gameService);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsLockedCube(Long gameId) {
        Game game = assertMockGameAndReset(gameId);

        game.setStatus(GameState.SETUP);
        assertFalse(gameDetailController.isLockedCube());
        game.setStatus(GameState.VALID_SETUP);
        assertFalse(gameDetailController.isLockedCube());
        game.setStatus(GameState.HALTED);
        assertFalse(gameDetailController.isLockedCube());

        game.setStatus(GameState.PLAYED);
        assertTrue(gameDetailController.isLockedCube());
        game.setStatus(GameState.FINISHED);
        assertTrue(gameDetailController.isLockedCube());
        game.setStatus(GameState.CANCELED);
        assertTrue(gameDetailController.isLockedCube());

        verifyNoInteractions(gameService);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoConfirm(Long gameId) {
        User user = createUser(7L);
        Game game = assertMockGame(gameId);

        gameDetailController.doConfirm(user);

        verify(gameService).confirm(user, game);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoReloadGame(Long gameId) {
        assertMockGame(gameId);

        gameDetailController.doReloadGame();

        verify(gameService, times(2)).loadGame(gameId);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveGame(Long gameId) {
        Game game = assertMockGame(gameId, true, false);
        when(gameService.saveGame(game)).thenReturn(game);

        gameDetailController.doSaveGame();

        verify(gameService).saveGame(game);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveGameSetup(Long gameId) {
        Game game = assertMockGame(gameId, true, false);
        game.setStatus(GameState.SETUP);
        when(gameService.saveGame(game)).thenReturn(game);

        gameDetailController.doSaveGame();

        verify(gameService).saveGame(game);
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveGameSetupCubeSwitched(Long gameId) {
        Game game = assertMockGame(gameId, true, false);
        game.setStatus(GameState.SETUP);
        game.setCube(createCube(15L));
        when(gameService.saveGame(game)).thenReturn(game);

        gameDetailController.doSaveGame();

        verify(gameService).saveGame(game);
        verify(cubeStatusController).switchCube(any(Cube.class), any(Cube.class));
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveTermFailure(Long termId) {
        Game game = assertMockGame(termId, true, false);
        when(gameService.saveGame(game)).thenReturn(null);

        gameDetailController.doSaveGame();

        verify(gameService).saveGame(game);
        verifyNoInteractions(messageBean);
        assertEquals(game, gameDetailController.getGame());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveGameInvalid(Long gameId) {
        assertMockGame(gameId);
        reset(gameService);

        gameDetailController.doSaveGame();

        verifyNoInteractions(gameService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
        assertEquals(gameId, gameDetailController.getGame().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 40, 444 })
    public void testDoDeleteGame(Long gameId) {
        Game game = assertMockGame(gameId);

        gameDetailController.doDeleteGame();

        verify(gameService).deleteGame(game);
        assertNull(gameDetailController.getGame());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoValidateGame(Long gameId) {
        Game game = assertMockGameAndReset(gameId);

        assertFalse(gameDetailController.doValidateGame());
        game.setName("agame");
        assertFalse(gameDetailController.doValidateGame());
        game.setMaxPoints(20);
        assertFalse(gameDetailController.doValidateGame());

        Cube cube = null;
        game.setCube(cube);
        assertFalse(gameDetailController.doValidateGame());
        cube = new Cube();
        game.setCube(cube);
        assertFalse(gameDetailController.doValidateGame());
        cube = createCube(0L);
        game.setCube(cube);
        assertFalse(gameDetailController.doValidateGame());
        cube.setId(9L);
        game.setCube(cube);
        assertFalse(gameDetailController.doValidateGame());

        Topic topic = createTopic(5L);
        topic.setEnabled(false);
        game.setTopic(topic);
        assertFalse(gameDetailController.doValidateGame());
        topic.setEnabled(true);
        assertFalse(gameDetailController.doValidateGame());

        game.setTeams(Set.of(createTeam(2L)));
        assertFalse(gameDetailController.doValidateGame());
        Set<Team> teams = game.getTeams();
        teams.add(createTeam(3L));
        game.setTeams(teams);
        assertTrue(gameDetailController.doValidateGame());

        verifyNoInteractions(gameService);
    }

    private void assertCanTraverse(GameState from, GameState to, boolean expected) {
        gameDetailController.getGame().setStatus(from);
        assertEquals(expected, gameDetailController.canTraverse(to));
    }

    private Game assertMockGameAndReset(Long gameId) {
        return assertMockGame(gameId, false, true);
    }

    private Game assertMockGame(Long gameId) {
        return assertMockGame(gameId, false, false);
    }

    private Game assertMockGame(Long gameId, boolean full, boolean resetService) {
        Game game = createGame(gameId);
        if (full) {
            game.setName("agame");
            game.setMaxPoints(200);
            game.setCube(createCube(100L));
            game.setTopic(createTopic(15L));
            game.setTeams(Set.of(createTeam(2L), createTeam(15L)));
        }
        when(gameService.loadGame(gameId)).thenReturn(game);

        gameDetailController.setGame(game);

        verify(gameService).loadGame(gameId);
        assertEquals(gameId, gameDetailController.getGame().getId());
        if (resetService) reset(gameService);

        return game;
    }
}
