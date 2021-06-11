package at.timeguess.backend.ui.controllers;

import static at.timeguess.backend.utils.TestSetup.createCube;
import static at.timeguess.backend.utils.TestSetup.createCubeFace;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.PushContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import at.timeguess.backend.events.ChannelPresenceEvent;
import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.model.Activity;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.model.Validation;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.controllers.GameManagerController.NoGameReason;
import at.timeguess.backend.ui.controllers.GameManagerController.RoundState;
import at.timeguess.backend.ui.controllers.GameManagerController.WaitReason;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Run Tests for {@link GameManagerController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class GameManagerControllerRunTest {

    @Autowired
    @InjectMocks
    private GameManagerController gameManagerController;

    @Autowired
    private GameService gameService;

    @Mock
    private WebSocketManager websocketManager;
    @Mock
    private PushContext messageChannel;
    @Mock
    private PushContext newRoundChannel;

    @BeforeEach
    public void beforeEach() {
        ReflectionTestUtils.setField(gameManagerController, "websocketManager", websocketManager);
        reset(messageChannel);
        reset(newRoundChannel);
        when(websocketManager.getMessageChannel()).thenReturn(messageChannel);
        when(websocketManager.getNewRoundChannel()).thenReturn(newRoundChannel);
    }

    @ParameterizedTest
    @ValueSource(longs = { 8 })
    @DirtiesContext
    public void testRunFinish(Long gameId) {
        try {
            Game game = createGame(gameId);

            testGameAdd(game);
            testGameCurrent(game);

            beforeEach();
            testGameStart(game, Set.of(6L, 7L));

            beforeEach();
            testGamePlayBasic(game);

            beforeEach();
            testGamePlayExtended(game);

        }
        catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(longs = { 9 })
    @DirtiesContext
    public void testRunCancel(Long gameId) {
        try {
            Game game = createGame(gameId);

            testGameAdd(game);
            testGameCurrent(game);

            beforeEach();
            testGameStart(game, Set.of(2L, 9L));

            beforeEach();
            testGamePlayBasic(game);

            beforeEach();
            testGameCancel(game);

        }
        catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    public void testRun1(Long gameId) {
        // cannot add null
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(null));

        // cannot add game in wrong state
        Game game = gameService.loadGame(gameId);
        game.setStatus(GameState.FINISHED);
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(game));

        // cannot add game with unknown cube
        game.setStatus(GameState.SETUP);
        game.setCube(createCube(15L));
        assertDoesNotThrow(() -> gameManagerController.addGame(game));

        // add valid game
        game.setCube(createCube(100L, "mac"));
        assertDoesNotThrow(() -> gameManagerController.addGame(game));
        verify(messageChannel).send(anyMap(), anySet());

        // can handle add game twice
        game.setStatus(GameState.SETUP);
        assertDoesNotThrow(() -> gameManagerController.addGame(game));
        verify(messageChannel, times(2)).send(anyMap(), anySet());

        // cannot add other game for same cube
        Game game2 = gameService.loadGame(gameId + 1);
        game2.setStatus(GameState.SETUP);
        game2.setCube(game.getCube());
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(game2));

        // enough players present
        game.setStatus(GameState.VALID_SETUP);
        gameManagerController.onChannelPresenceEvent(new ChannelPresenceEvent(this, "newRoundChannel", Set.of(6L, 7L)));
        assertEquals(GameState.PLAYED, game.getStatus());
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        verify(newRoundChannel).send(anyString(), anySet());

        assertTrue(true);
    }

    private void testGameAdd(Game game) {
        // cannot add null
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(null));
        assertNotNull(gameManagerController.getCurrentRoundForGame(null));

        // cannot add game in wrong state
        game.setStatus(GameState.FINISHED);
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(game));

        // cannot add game with unknown cube
        game.setStatus(GameState.SETUP);
        game.setCube(createCube(15L));
        assertDoesNotThrow(() -> gameManagerController.addGame(game));

        // add valid game
        game.setCube(createCube(100L, "mac"));
        assertDoesNotThrow(() -> gameManagerController.addGame(game));
        verify(messageChannel).send(anyMap(), anySet());

        // can handle add game twice
        game.setStatus(GameState.SETUP);
        assertDoesNotThrow(() -> gameManagerController.addGame(game));
        verify(messageChannel, times(2)).send(anyMap(), anySet());

        // cannot add other game for same cube
        Game game2 = gameService.loadGame(game.getId() - 1);
        game2.setStatus(GameState.SETUP);
        game2.setCube(game.getCube());
        assertThrows(IllegalArgumentException.class, () -> gameManagerController.addGame(game2));
    }

    private void testGameCurrent(Game game) {
        Optional<User> user = game.getTeams().stream()
            .flatMap(t -> t.getTeamMembers().stream().map(u -> u))
            .findFirst();

        assertTrue(user.isPresent());
        assertTrue(gameManagerController.hasCurrentGameForCube(game.getCube()));
        assertEquals(game, gameManagerController.getCurrentGameForUser(user.get()));
        assertNull(gameManagerController.getCurrentRoundForGame(game));
    }

    private void testGameStart(Game game, Set<Long> usersPresent) {
        // enough players present
        game.setStatus(GameState.VALID_SETUP);
        assertEquals(WaitReason.TEAMS_ABSENT, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.NONE, gameManagerController.getRoundStateForGame(game));

        gameManagerController.onChannelPresenceEvent(new ChannelPresenceEvent(this, "newRoundChannel", usersPresent));

        assertEquals(GameState.PLAYED, game.getStatus());
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        assertNotNull(gameManagerController.getCurrentRoundForGame(game));
        verify(newRoundChannel).send(anyString(), anySet());
    }

    private void testGamePlayBasic(Game game) throws InterruptedException {
        // no validation possible in round state starting
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        gameManagerController.validateRoundOfGame(game, Validation.CHEATED);
        verifyNoInteractions(newRoundChannel);

        // start countdown
        var event = new ConfiguredFacetsEvent(this, game.getCube(), createCubeFace("id", 2, 1, Activity.DRAW));
        gameManagerController.onConfiguredFacetsEvent(event);

        assertEquals(RoundState.RUNNING, gameManagerController.getRoundStateForGame(game));
        verify(newRoundChannel).send(anyString(), anySet());

        Thread.sleep(1010);
        assertEquals(120, gameManagerController.getTimerForGame(game));
        Thread.sleep(1010);
        assertEquals("1 : 59", gameManagerController.getCountDownForGame(game));

        // start validation
        gameManagerController.onConfiguredFacetsEvent(event);
        assertEquals(RoundState.VALIDATING, gameManagerController.getRoundStateForGame(game));
        verify(newRoundChannel, times(2)).send(anyString(), anySet());

        gameManagerController.validateRoundOfGame(game, Validation.CORRECT);

        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        verify(newRoundChannel, times(3)).send(anyString(), anySet());
    }

    private void testGamePlayExtended(Game game) {
        // no validation possible in round state starting
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        gameManagerController.validateRoundOfGame(game, Validation.INCORRECT);
        verifyNoInteractions(newRoundChannel);

        // set cube offline
        gameManagerController.cubeOffline(game.getCube());
        assertEquals(GameState.VALID_SETUP, game.getStatus());
        assertEquals(WaitReason.CUBE_OFFLINE, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        assertNotNull(gameManagerController.getCurrentRoundForGame(game));
        verify(newRoundChannel).send(anyString(), anySet());

        // no validation possible in round state starting
        gameManagerController.validateRoundOfGame(game, Validation.CHEATED);
        verifyNoMoreInteractions(newRoundChannel);

        // set error state
        var event = new ConfiguredFacetsEvent(this, game.getCube(), createCubeFace("id", 2, 1, Activity.DRAW));
        gameManagerController.onConfiguredFacetsEvent(event);

        // no validation possible in round state error
        gameManagerController.validateRoundOfGame(game, Validation.CORRECT);
        verifyNoMoreInteractions(newRoundChannel);

        // set cube online
        gameManagerController.cubeOnline(game.getCube());
        assertEquals(GameState.PLAYED, game.getStatus());
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.STARTING, gameManagerController.getRoundStateForGame(game));
        assertNotNull(gameManagerController.getCurrentRoundForGame(game));
        verify(newRoundChannel, times(2)).send(anyString(), anySet());

        // start countdown
        gameManagerController.onConfiguredFacetsEvent(event);
        assertEquals(RoundState.RUNNING, gameManagerController.getRoundStateForGame(game));
        verify(newRoundChannel, times(3)).send(anyString(), anySet());

        // set cube off and on, no error state
        gameManagerController.cubeOffline(game.getCube());
        verify(newRoundChannel, times(4)).send(anyString(), anySet());

        gameManagerController.cubeOnline(game.getCube());
        verify(newRoundChannel, times(5)).send(anyString(), anySet());

        gameManagerController.onConfiguredFacetsEvent(event);

        // validation possible, game finished
        gameManagerController.validateRoundOfGame(game, Validation.CORRECT);
        assertEquals(GameState.FINISHED, game.getStatus());
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.NONE, gameManagerController.getRoundStateForGame(game));
        assertEquals(NoGameReason.GAME_UNKNOWN, gameManagerController.getNoGameReasonForGame(game));
        verify(newRoundChannel, times(7)).send(anyString(), anySet());
    }

    private void testGameCancel(Game game) {
        game.setStatus(GameState.CANCELED);
        gameManagerController.removeGame(game);

        assertEquals(GameState.CANCELED, game.getStatus());
        assertEquals(WaitReason.NONE, gameManagerController.getWaitReasonForGame(game));
        assertEquals(RoundState.NONE, gameManagerController.getRoundStateForGame(game));
        assertEquals(NoGameReason.GAME_UNKNOWN, gameManagerController.getNoGameReasonForGame(game));
        verify(newRoundChannel).send(anyString(), anySet());
    }

    private Game createGame(Long gameId) {
        Game game = gameService.loadGame(gameId);

        // manipulate game to be able to finish a game
        game.setMaxPoints(2);
        game.setTeams(Set.of(game.getTeams().toArray(Team[]::new)[0]));
        return game;
    }
}
