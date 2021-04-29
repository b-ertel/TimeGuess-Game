package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
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
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link NewTeamBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class NewTeamBeanTest {

    @InjectMocks
    private NewTeamBean newTeamBean;

    @Mock
    private TeamService teamService;
    @Mock
    private UserService userService;
    @Mock
    private MessageBean messageBean;

    @BeforeEach
    public void beforeEach() {
        newTeamBean.clearFields();
    }

    @Test
    public void testGetAllPlayers() {
        List<User> expected = createEntities(TestSetup::createUser, 10);
        when(userService.getAllPlayers()).thenReturn(expected);

        List<User> result = newTeamBean.getAllPlayers();

        verify(userService).getAllPlayers();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsAvailablePlayer(Long gameId) {
        User user = createUser(6L);

        when(userService.isAvailablePlayer(user)).thenReturn(true);
        assertTrue(newTeamBean.isAvailablePlayer(user));
        verify(userService).isAvailablePlayer(user);

        reset(userService);
        when(userService.isAvailablePlayer(user)).thenReturn(false);
        assertFalse(newTeamBean.isAvailablePlayer(user));
        verify(userService).isAvailablePlayer(user);
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    public void testClearFields(Integer roleNr) {
        String name = fillBean();

        assertEquals(name, newTeamBean.getTeamName());
        assertTrue(newTeamBean.getPlayers().size() > 0);

        newTeamBean.clearFields();

        assertNull(newTeamBean.getTeamName());
        assertNull(newTeamBean.getPlayers());
    }

    @Test
    public void testCreateTeam() {
        String name = fillBean();
        Team expected = createTeam(6L);
        expected.setName(name);
        when(teamService.saveTeam(any(Team.class))).thenReturn(expected);

        Team result = newTeamBean.createTeam();

        verify(teamService).saveTeam(any(Team.class));
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);

        assertTrue(expected.compareTo(result) == 0);
        assertNull(result.getGames());

        Set<Game> games = null;
        result.setGames(games);
        assertEquals(games, result.getGames());
        games = new HashSet<>();
        result.setGames(games);
        assertEquals(games, result.getGames());
        games.add(createGame(8L));
        result.setGames(games);
        assertEquals(games, result.getGames());
    }

    @Test
    public void testCreateTeamFailure() {
        newTeamBean.createTeam();

        verifyNoInteractions(teamService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testValidateInput() {
        newTeamBean.setTeamName(null);
        assertFalse(newTeamBean.validateInput());
        newTeamBean.setTeamName("");
        assertFalse(newTeamBean.validateInput());
        newTeamBean.setTeamName("ateam");
        assertFalse(newTeamBean.validateInput());

        Set<User> users = null;
        newTeamBean.setPlayers(users);
        assertFalse(newTeamBean.validateInput());
        users = new HashSet<>();
        newTeamBean.setPlayers(users);
        assertFalse(newTeamBean.validateInput());
        users.add(createUser(8L));
        newTeamBean.setPlayers(users);
        assertFalse(newTeamBean.validateInput());
        users.add(createUser(9L));
        newTeamBean.setPlayers(users);
        assertTrue(newTeamBean.validateInput());

        verifyNoInteractions(teamService);
    }

    private String fillBean() {
        String foo = "foobarbat";

        newTeamBean.setTeamName(foo);
        newTeamBean.setPlayers(Set.of(createUser(2L), createUser(15L)));
        return foo;
    }
}
