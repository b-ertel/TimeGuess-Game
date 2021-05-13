package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createTeam;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

import at.timeguess.backend.model.Team;
import at.timeguess.backend.repositories.TeamRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link TeamService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TeamServiceMockedTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private MessageBean messageBean;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testSaveTeam() {
        Team team = createTeam(5L);
        team.setName("aname");
        PushContext context = mock(PushContext.class);
        when(teamRepository.save(team)).thenReturn(team);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> teamService.saveTeam(team));

        verify(teamRepository).save(team);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }
}
