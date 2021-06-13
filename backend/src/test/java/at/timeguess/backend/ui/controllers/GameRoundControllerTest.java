package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createTeam;
import static at.timeguess.backend.utils.TestSetup.createUser;
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
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.model.utils.TeamScore;
import at.timeguess.backend.services.HighscoreService;

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

    @Test
    public void testCorrectRound() {
        assertRoundValidataion(Validation.CORRECT, () -> gameRoundController.correctRound());
    }

    @Test
    public void testIncorrectRound() {
        assertRoundValidataion(Validation.INCORRECT, () -> gameRoundController.incorrectRound());
    }

    @Test
    public void testCheatedRound() {
        assertRoundValidataion(Validation.CHEATED, () -> gameRoundController.cheatedRound());
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

    public void assertRoundValidataion(Validation expected, Runnable run) {
        ArgumentCaptor<Validation> arg = ArgumentCaptor.forClass(Validation.class);

        run.run();

        verify(gameManagerController).validateRoundOfGame(nullable(Game.class), arg.capture());
        assertEquals(expected, arg.getValue());
    }
}
