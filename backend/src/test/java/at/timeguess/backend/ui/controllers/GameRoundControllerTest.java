package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.model.utils.TeamScore;
import at.timeguess.backend.services.HighscoreService;
import at.timeguess.backend.ui.controllers.GameManagerController.NoGameReason;
import at.timeguess.backend.ui.controllers.GameManagerController.RoundState;
import at.timeguess.backend.ui.controllers.GameManagerController.WaitReason;
import at.timeguess.backend.ui.controllers.GameRoundController.RunState;

/**
 * Tests for {@link GameRoundController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameRoundControllerTest {

    @InjectMocks
    private GameRoundController gameRoundController;

    @Mock
    private GameManagerController gameManagerController;
    @Mock
    private HighscoreService highscoreService;

    @ParameterizedTest
    @ValueSource(longs = { 9 })
    public void testGetSetUser(Long userId) {
        User user = createUser(userId);

        gameRoundController.setUser(user);

        assertEquals(userId, gameRoundController.getUser().getId());
        assertNull(gameRoundController.getCurrentGame());
        assertNull(gameRoundController.getLastRound());
        assertNull(gameRoundController.getCurrentRound());
    }

    @Test
    public void testGetCountDown() {
        String expected = "time";
        when(gameManagerController.getCountDownForGame(nullable(Game.class))).thenReturn(expected);

        String result = gameRoundController.getCountDown();

        verify(gameManagerController).getCountDownForGame(nullable(Game.class));
        assertEquals(expected, result);
    }

    @Test
    public void testGetTimer() {
        int expected = 55;
        when(gameManagerController.getTimerForGame(nullable(Game.class))).thenReturn(expected);

        int result = gameRoundController.getTimer();

        verify(gameManagerController).getTimerForGame(nullable(Game.class));
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testGetCurrentRunState(Long gameId) {
        User user = createUser(gameId);
        gameRoundController.setUser(user);

        assertEquals(RunState.NONE, gameRoundController.getCurrentRunState());

        Game game = createGame(gameId);
        when(gameManagerController.getCurrentGameForUser(user)).thenReturn(game);

        game.setStatus(GameState.FINISHED);
        assertEquals(RunState.NONE, gameRoundController.getCurrentRunState());

        game.setStatus(GameState.CANCELED);
        assertEquals(RunState.NONE, gameRoundController.getCurrentRunState());

        game.setStatus(GameState.SETUP);
        assertEquals(RunState.NONE, gameRoundController.getCurrentRunState());

        game.setStatus(GameState.VALID_SETUP);
        assertEquals(RunState.WAITING, gameRoundController.getCurrentRunState());

        game.setStatus(GameState.HALTED);
        assertEquals(RunState.WAITING, gameRoundController.getCurrentRunState());

        game.setStatus(GameState.PLAYED);
        assertEquals(RunState.RUNNING, gameRoundController.getCurrentRunState());
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testGetCurrentRoundState(Long gameId) {
        User user = createUser(gameId);
        gameRoundController.setUser(user);

        assertEquals(RoundState.NONE, gameRoundController.getCurrentRoundState());

        Game game = createGame(gameId);
        game.setStatus(GameState.PLAYED);
        when(gameManagerController.getCurrentGameForUser(user)).thenReturn(game);
        when(gameManagerController.getRoundStateForGame(game)).thenReturn(RoundState.RUNNING);

        assertEquals(RoundState.RUNNING, gameRoundController.getCurrentRoundState());
        verify(gameManagerController, times(3)).getCurrentGameForUser(user);
        verify(gameManagerController).getRoundStateForGame(game);
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testGetCurrentWaitReason(Long gameId) {
        User user = createUser(gameId);
        gameRoundController.setUser(user);

        assertEquals(WaitReason.NONE, gameRoundController.getCurrentWaitReason());

        Game game = createGame(gameId);
        game.setStatus(GameState.HALTED);
        when(gameManagerController.getCurrentGameForUser(user)).thenReturn(game);
        when(gameManagerController.getWaitReasonForGame(game)).thenReturn(WaitReason.GAME_HALTED);

        assertEquals(WaitReason.GAME_HALTED, gameRoundController.getCurrentWaitReason());
        verify(gameManagerController, times(3)).getCurrentGameForUser(user);
        verify(gameManagerController).getWaitReasonForGame(game);
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testGetCurrentNoGameReason(Long gameId) {
        User user = createUser(gameId);
        gameRoundController.setUser(user);
        Game game = createGame(gameId);
        game.setStatus(GameState.VALID_SETUP);
        when(gameManagerController.getCurrentGameForUser(user)).thenReturn(game);

        assertEquals(NoGameReason.NONE, gameRoundController.getCurrentNoGameReason());

        game.setStatus(GameState.FINISHED);
        when(gameManagerController.getNoGameReasonForGame(game)).thenReturn(NoGameReason.GAME_FINISHED);

        assertEquals(NoGameReason.GAME_FINISHED, gameRoundController.getCurrentNoGameReason());
        verify(gameManagerController, times(2)).getCurrentGameForUser(user);
        verify(gameManagerController).getNoGameReasonForGame(game);
    }

    @ParameterizedTest
    @ValueSource(longs = { 5, 15 })
    public void testIsRoundRunning(Long gameId) {
        assertFalse(gameRoundController.isRoundRunning());

        User user = createUser(gameId);
        gameRoundController.setUser(user);
        Game game = createGame(gameId);
        when(gameManagerController.getCurrentGameForUser(user)).thenReturn(game);
        when(gameManagerController.getRoundStateForGame(game)).thenReturn(RoundState.STARTING);

        assertFalse(gameRoundController.isRoundRunning());

        when(gameManagerController.getRoundStateForGame(game)).thenReturn(RoundState.RUNNING);

        assertTrue(gameRoundController.isRoundRunning());
        verify(gameManagerController, times(2)).getCurrentGameForUser(user);
        verify(gameManagerController, times(2)).getRoundStateForGame(game);
    }

    @Test
    public void testCorrectRound() {
        assertRoundValidation(Validation.CORRECT, () -> gameRoundController.correctRound());
    }

    @Test
    public void testIncorrectRound() {
        assertRoundValidation(Validation.INCORRECT, () -> gameRoundController.incorrectRound());
    }

    @Test
    public void testCheatedRound() {
        assertRoundValidation(Validation.CHEATED, () -> gameRoundController.cheatedRound());
    }

    @Test
    public void testCalculatePointsOfTeam() {
        Team team = createTeam(10L);
        Integer expected = 10;
        when(gameManagerController.getPointsOfTeamForGame(nullable(Game.class), any(Team.class))).thenReturn(expected);

        Integer result = gameRoundController.calculatePointsOfTeam(team);

        verify(gameManagerController).getPointsOfTeamForGame(nullable(Game.class), any(Team.class));
        assertEquals(expected, result);
    }

    @Test
    public void testComputeWinningTeam() {
        Team expected = createTeam(10L);
        when(gameManagerController.getTeamWithMostPointsForGame(nullable(Game.class))).thenReturn(expected);

        Team result = gameRoundController.computeWinningTeam();

        verify(gameManagerController).getTeamWithMostPointsForGame(nullable(Game.class));
        assertEquals(expected, result);
    }

    @Test
    public void testComputeFinalRanking() {
        assertEquals(0, gameRoundController.computeFinalRanking().size());

        when(highscoreService.getTeamRanking(nullable(Game.class))).thenReturn(null);
        assertNull(gameRoundController.computeFinalRanking());

        List<TeamScore> expected = List.of();
        when(highscoreService.getTeamRanking(nullable(Game.class))).thenReturn(expected);

        assertEquals(expected, gameRoundController.computeFinalRanking());

        verify(highscoreService, times(3)).getTeamRanking(nullable(Game.class));
    }

    public void assertRoundValidation(Validation expected, Runnable run) {
        ArgumentCaptor<Validation> arg = ArgumentCaptor.forClass(Validation.class);

        run.run();

        verify(gameManagerController).validateRoundOfGame(nullable(Game.class), arg.capture());
        assertEquals(expected, arg.getValue());
    }
}
