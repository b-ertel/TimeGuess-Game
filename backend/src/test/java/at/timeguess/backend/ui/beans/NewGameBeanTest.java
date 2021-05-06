package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link NewGameBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class NewGameBeanTest {

    @InjectMocks
    private NewGameBean newGameBean;

    @Mock
    private GameService gameService;
    @Mock
    private TeamService teamService;
    @Mock
    private MessageBean messageBean;

    @BeforeEach
    public void beforeEach() {
        newGameBean.clearFields();
    }

    @Test
    public void testGetAllTeams() {
        List<Team> expected = createEntities(TestSetup::createTeam, 10);
        when(teamService.getAllTeams()).thenReturn(expected);

        List<Team> result = newGameBean.getAllTeams();

        verify(teamService).getAllTeams();
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testIsAvailableTeam(Long teamId) {
        Team team = createTeam(teamId);

        when(teamService.isAvailableTeam(team)).thenReturn(true);
        assertTrue(newGameBean.isAvailableTeam(team));
        verify(teamService).isAvailableTeam(team);

        reset(teamService);
        when(teamService.isAvailableTeam(team)).thenReturn(false);
        assertFalse(newGameBean.isAvailableTeam(team));
        verify(teamService).isAvailableTeam(team);
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testAddNewTeam(Long teamId) {
        assertDoesNotThrow(() -> newGameBean.addNewTeam(null));

        Team team6 = createTeam(teamId);
        newGameBean.addNewTeam(team6);
        assertTrue(newGameBean.getTeams().contains(team6));

        Team team8 = createTeam(8L);
        newGameBean.addNewTeam(team8);
        assertTrue(newGameBean.getTeams().contains(team8));

        assertTrue(newGameBean.getTeams().contains(team6));
        assertEquals(2, newGameBean.getTeams().size());

        verifyNoInteractions(teamService);
    }

    @Test
    public void testClearFields() {
        String name = fillBean();

        assertEquals(name, newGameBean.getGameName());
        assertTrue(newGameBean.getMaxPoints() > 0);
        assertNotNull(newGameBean.getTopic());
        assertNotNull(newGameBean.getCube());
        assertTrue(newGameBean.getTeams().size() > 0);

        newGameBean.clearFields();

        assertNull(newGameBean.getGameName());
        assertEquals(0, newGameBean.getMaxPoints());
        assertNull(newGameBean.getTopic());
        assertNull(newGameBean.getCube());
        assertNull(newGameBean.getTeams());
    }

    @Test
    public void testCreateGame() {
        String name = fillBean();
        Game expected = createGame(6L);
        expected.setName(name);
        when(gameService.saveGame(any(Game.class))).thenReturn(expected);

        Game result = newGameBean.createGame();

        verify(gameService).saveGame(any(Game.class));
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);
    }

    @Test
    public void testCreateGameFailure() {
        newGameBean.createGame();

        verifyNoInteractions(gameService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testValidateInput() {
        newGameBean.setGameName(null);
        assertFalse(newGameBean.validateInput());
        newGameBean.setGameName("");
        assertFalse(newGameBean.validateInput());
        newGameBean.setGameName("agame");
        assertFalse(newGameBean.validateInput());

        newGameBean.setMaxPoints(20);
        assertFalse(newGameBean.validateInput());

        Cube cube = null;
        newGameBean.setCube(cube);
        assertFalse(newGameBean.validateInput());
        cube = new Cube();
        newGameBean.setCube(cube);
        assertFalse(newGameBean.validateInput());
        cube = createCube(0L);
        newGameBean.setCube(cube);
        assertFalse(newGameBean.validateInput());
        cube.setId(9L);
        newGameBean.setCube(cube);
        assertFalse(newGameBean.validateInput());

        newGameBean.setTopic(createTopic(3L));
        assertFalse(newGameBean.validateInput());

        Set<Team> teams = null;
        newGameBean.setTeams(teams);
        assertFalse(newGameBean.validateInput());
        teams = new HashSet<>();
        newGameBean.setTeams(teams);
        assertFalse(newGameBean.validateInput());
        teams.add(createTeam(8L));
        newGameBean.setTeams(teams);
        assertFalse(newGameBean.validateInput());
        teams.add(createTeam(9L));
        newGameBean.setTeams(teams);
        assertTrue(newGameBean.validateInput());

        verifyNoInteractions(gameService);
    }

    @Test
    public void testMessageBeanNoContext() {
        MessageBean bean = new MessageBean();
        assertDoesNotThrow(() -> bean.alertInformation("header", "text"));
        assertDoesNotThrow(() -> bean.alertErrorFailValidation("header", "text"));
    }

    private String fillBean() {
        String foo = "foobarbat";

        newGameBean.setGameName(foo);
        newGameBean.setMaxPoints(100);
        newGameBean.setCube(createCube(6L));
        newGameBean.setTopic(createTopic(12L));
        newGameBean.setTeams(Set.of(createTeam(2L), createTeam(15L)));
        return foo;
    }
}
