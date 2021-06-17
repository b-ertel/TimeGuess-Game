package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createRound;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.GameLogicService;
import at.timeguess.backend.services.RoundService;
import at.timeguess.backend.ui.controllers.GameManagerController.GameData;
import at.timeguess.backend.ui.controllers.GameManagerController.NoGameReason;
import at.timeguess.backend.ui.controllers.GameManagerController.RoundState;
import at.timeguess.backend.ui.controllers.GameManagerController.Status;
import at.timeguess.backend.ui.controllers.GameManagerController.WaitReason;

/**
 * Tests for {@link GameManagerController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameManagerControllerTest {

    @InjectMocks
    private GameManagerController gameManagerController;

    @Mock
    private Status status;
    @Mock
    private RoundService roundService;
    @Mock
    private GameLogicService gameLogicService;

    @ParameterizedTest
    @ValueSource(longs = { 18 })
    public void testGetRoundState(Long gameId) {
        Game game = createGame(gameId);
        GameData gameData = new GameData(game, gameManagerController.new CountDown());

        assertEquals(RoundState.NONE, gameManagerController.getRoundStateForGame(null));

        when(status.getGameData(game)).thenReturn(gameData);
        assertEquals(RoundState.NONE, gameManagerController.getRoundStateForGame(game));

        gameData.currentRound = createRound(1);
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));

        gameData.isRoundActive = true;
        assertEquals(RoundState.RUNNING, gameManagerController.getRoundStateForGame(game));

        gameData.isValidating = true;
        assertEquals(RoundState.ERROR, gameManagerController.getRoundStateForGame(game));

        gameData.isRoundActive = false;
        assertEquals(RoundState.VALIDATING, gameManagerController.getRoundStateForGame(game));

        verify(status, times(5)).getGameData(game);
    }

    @ParameterizedTest
    @ValueSource(longs = { 28 })
    public void testGetWaitReason(Long gameId) {
        Game game = createGame(gameId);
        GameData gameData = new GameData(game, gameManagerController.new CountDown());

        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(null));

        when(status.getGameData(game)).thenReturn(gameData);
        game.setStatus(GameState.FINISHED);
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));

        game.setStatus(GameState.HALTED);
        assertEquals(WaitReason.GAME_HALTED, gameManagerController.getWaitReasonForGame(game));

        game.setStatus(GameState.VALID_SETUP);
        gameData.isCubeOnline = false;
        assertEquals(WaitReason.CUBE_OFFLINE, gameManagerController.getWaitReasonForGame(game));

        gameData.isCubeOnline = true;
        gameData.isTeamsPresent = false;
        assertEquals(WaitReason.TEAMS_ABSENT, gameManagerController.getWaitReasonForGame(game));

        gameData.isTeamsPresent = true;
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));

        verify(status, times(5)).getGameData(game);
    }

    @ParameterizedTest
    @ValueSource(longs = { 38 })
    public void testGetNoGameReason(Long gameId) {
        Game game = createGame(gameId);// assertLoadGame(3L);
        GameData gameData = new GameData(game, gameManagerController.new CountDown());

        assertEquals(NoGameReason.GAME_UNKNOWN, gameManagerController.getNoGameReasonForGame(null));

        when(status.getGameData(game)).thenReturn(gameData);
        game.setStatus(GameState.FINISHED);
        assertEquals(NoGameReason.GAME_FINISHED, gameManagerController.getNoGameReasonForGame(game));

        game.setStatus(GameState.CANCELED);
        assertEquals(NoGameReason.GAME_CANCELED, gameManagerController.getNoGameReasonForGame(game));

        game.setStatus(GameState.SETUP);
        assertEquals(NoGameReason.GAME_WRONGSTATE, gameManagerController.getNoGameReasonForGame(game));

        game.setStatus(GameState.PLAYED);
        assertEquals(NoGameReason.NONE, gameManagerController.getNoGameReasonForGame(game));

        verify(status, times(4)).getGameData(game);
    }

    @Test
    public void testGameDataInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new GameData(null, null));
    }

    @ParameterizedTest
    @ValueSource(longs = { 48 })
    public void testGetPointsOfTeamForGame(Long gameId) {
        Game game = createGame(gameId);
        Team team = createTeam(gameId);
        Integer expected = 10;
        when(roundService.getPointsOfTeamInGame(game, team)).thenReturn(expected);

        Integer result = gameManagerController.getPointsOfTeamForGame(game, team);

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 48 })
    public void testGetTeamWithMostPointsForGame(Long gameId) {
        Game game = createGame(gameId);
        Team expected = createTeam(gameId);
        when(gameLogicService.getTeamWithMostPoints(game)).thenReturn(expected);

        Team result = gameManagerController.getTeamWithMostPointsForGame(game);

        assertEquals(expected, result);
    }
}
