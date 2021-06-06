package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createRound;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.RoundRepository;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link RoundService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class RoundServiceMockedTest {

    @InjectMocks
    private RoundService roundService;

    @Mock
    private RoundRepository roundRepository;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testGetPointsOfTeamInGame() {
        Game game = null;
        Team team = null;
        when(roundRepository.getPointsOfTeamInGame(game, team)).thenReturn(null);

        Integer result = roundService.getPointsOfTeamInGame(game, team);

        assertEquals(0, result);
    }

    @Test
    public void testSaveRound() {
        Round round = createRound(5L);
        PushContext context = mock(PushContext.class);
        when(roundRepository.save(round)).thenReturn(round);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> roundService.saveRound(round));

        verify(roundRepository).save(round);
        verify(context).send(anyMap());
    }

    @Test
    public void testSaveNoSuccess() {
        Round round = createRound(5L);
        PushContext context = mock(PushContext.class);
        when(roundRepository.save(round)).thenThrow(RuntimeException.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> roundService.saveRound(round));

        verifyNoInteractions(context);
    }
}
