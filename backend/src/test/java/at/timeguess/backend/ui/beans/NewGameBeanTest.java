package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

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
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.services.TeamService;
import at.timeguess.backend.ui.controllers.CubeStatusController;
import at.timeguess.backend.ui.controllers.GameManagerController;
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
    private CubeStatusController cubeStatusController;
    @Mock
    private GameManagerController gameManagerController;
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

        newGameBean.setTeams(null);
        newGameBean.addNewTeam(team6);
        assertTrue(newGameBean.getTeams().contains(team6));

        verifyNoInteractions(teamService);
    }

    @Test
    public void testClearFields() {
        String expected = fillBean();
        assertFields(expected);

        newGameBean.clearFields();

        assertFieldsClear();
    }

    @Test
    public void testCreateGame() {
        String name = fillBean();
        Game expected = createGame(6L);
        expected.setName(name);
        when(gameService.saveGame(any(Game.class))).thenReturn(expected);

        Game result = newGameBean.createGame();

        verify(cubeStatusController).switchCube(nullable(Cube.class), any(Cube.class));
        verify(gameService).saveGame(any(Game.class));
        verifyNoMoreInteractions(cubeStatusController);
        verify(gameManagerController).addGame(any(Game.class));
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);
        assertFieldsClear();
    }

    @Test
    public void testCreateGameFailure() {
        newGameBean.createGame();

        verifyNoInteractions(gameService);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testCreateGameFailureSave() {
        String expected = fillBean();
        when(gameService.saveGame(any(Game.class))).thenReturn(null);

        Game result = newGameBean.createGame();

        verify(gameService).saveGame(any(Game.class));
        verifyNoInteractions(gameManagerController);
        verifyNoInteractions(messageBean);
        assertNull(result);
        assertFields(expected);
    }

    @Test
    public void testValidateInput() {
        newGameBean.setGameName(null);
        assertFalse(newGameBean.validateInput());
        newGameBean.setGameName("");
        assertFalse(newGameBean.validateInput());
        newGameBean.setGameName("agame");
        assertFalse(newGameBean.validateInput());

        newGameBean.setMaxPoints(-5);
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

        Topic topic = createTopic(3L);
        topic.setEnabled(false);
        newGameBean.setTopic(topic);
        assertFalse(newGameBean.validateInput());
        topic.setEnabled(true);
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
    public void testMessageBeanAlertNoContext() {
        MessageBean bean = new MessageBean();
        assertDoesNotThrow(() -> bean.alertInformation("header", "text"));
        assertDoesNotThrow(() -> bean.alertErrorFailValidation("header", "text"));
    }

    @Test
    public void testMessageBeanRedirectNoContext() {
        MessageBean bean = new MessageBean();
        if (FacesContext.getCurrentInstance() == null)
            assertDoesNotThrow(() -> bean.redirect("somewhere"));
        else
            assertThrows(NullPointerException.class, () -> bean.redirect("somewhere"));
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

    private void assertFields(String expected) {
        assertEquals(expected, newGameBean.getGameName());
        assertTrue(newGameBean.getMaxPoints() > 0);
        assertNotNull(newGameBean.getTopic());
        assertNotNull(newGameBean.getCube());
        assertTrue(newGameBean.getTeams().size() > 0);
    }

    private void assertFieldsClear() {
        assertNull(newGameBean.getGameName());
        assertEquals(10, newGameBean.getMaxPoints());
        assertNull(newGameBean.getTopic());
        assertNull(newGameBean.getCube());
        assertEquals(0, newGameBean.getTeams().size());
    }
}
