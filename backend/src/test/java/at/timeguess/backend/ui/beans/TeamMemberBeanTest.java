package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.TeamService;

/**
 * Tests for {@link TeamMemberBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class TeamMemberBeanTest {

    @InjectMocks
    private TeamMemberBean teamMemberBean;

    @Mock
    private TeamService teamService;

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testGetSetTeam(Long teamId) {
        Team team = null;
        teamMemberBean.setTeam(team);
        assertNull(teamMemberBean.getTeam());

        team = createTeam(teamId);
        teamMemberBean.setTeam(team);
        assertTrue(team == teamMemberBean.getTeam());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testSetGame(Long gameId) {
        when(teamService.getAllTeams()).thenReturn(List.of(createTeam(1L), createTeam(2L)));

        Game game = null;
        teamMemberBean.setGame(game);
        assertEquals(2, teamMemberBean.getTeams().size());
        verify(teamService).getAllTeams();

        game = createGame(gameId);
        game.setTeams(null);
        teamMemberBean.setGame(game);
        assertEquals(2, teamMemberBean.getTeams().size());
        verify(teamService, times(2)).getAllTeams();

        teamMemberBean.setGame(game);
        assertEquals(2, teamMemberBean.getTeams().size());

        game.setTeams(Set.of(createTeam(1L), createTeam(2L)));
        teamMemberBean.setGame(game);
        assertEquals(2, teamMemberBean.getTeams().size());

        game.getTeams().forEach(t -> t.setTeamMembers(Set.of(createUser(1L))));
        teamMemberBean.setGame(game);
        assertEquals(2, teamMemberBean.getTeams().size());
    }
}
