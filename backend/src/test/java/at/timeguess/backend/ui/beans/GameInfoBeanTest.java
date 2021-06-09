package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.createGame;
import static at.timeguess.backend.utils.TestSetup.createRound;
import static at.timeguess.backend.utils.TestSetup.createTeam;
import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.services.GameService;
import at.timeguess.backend.ui.beans.GameInfoBean.GameInfo;

/**
 * Tests for {@link gameInfoBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class GameInfoBeanTest {

    @InjectMocks
    private GameInfoBean gameInfoBean;

    @Mock
    private GameService gameService;

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testGetRounds(Long gameId) {
        Game game = null;
        gameInfoBean.setGame(game);
        assertNull(gameInfoBean.getRounds());

        game = createGame(gameId);
        gameInfoBean.setGame(game);
        assertEquals(0, gameInfoBean.getRounds().size());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testGetTeams(Long gameId) {
        Game game = null;
        gameInfoBean.setGame(game);
        assertEquals(new DefaultTreeNode(), gameInfoBean.getTeams());

        game = createGame(gameId);
        gameInfoBean.setGame(game);
        assertEquals(0, gameInfoBean.getTeams().getChildCount());

        String expected = "org";
        Team team1 = createTeam(5L);
        Team team2 = createTeam(9L);
        User user1 = createUser(11L);
        User user2 = createUser(21L);
        team1.setName(expected);
        team2.setName(expected);
        team1.setTeamMembers(Set.of(user1));
        team2.setTeamMembers(Set.of(user2));
        game.setTeams(Set.of(team1, team2));

        Round round1 = createRound(1);
        Round round2 = createRound(2);
        round1.setGuessingTeam(team1);
        round2.setGuessingTeam(team2);
        round1.setGuessingUser(user1);
        round2.setGuessingUser(user2);
        game.setRounds(Set.of(round1, round2));

        gameInfoBean.setGame(game);
        TreeNode result = gameInfoBean.getTeams();
        assertEquals(2, result.getChildCount());

        var nodes = result.getChildren();
        GameInfo gi1 = (GameInfo) nodes.get(0).getData();
        GameInfo gi2 = (GameInfo) nodes.get(1).getData();
        assertEquals(0, gi1.compareTo(gi2));

        expected = "changed";
        for (var node : nodes) {
            GameInfo gi = (GameInfo) node.getData();
            gi.setName(expected);
            assertEquals(expected, gi.getName());
            gi.setRounds(5);
            assertEquals("5", gi.getRounds());
            gi.setPoints(5);
            assertEquals("5", gi.getPoints());
        }
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoReloadGame(Long gameId) {
        Game game = createGame(gameId);
        when(gameService.loadGame(gameId)).thenReturn(game);

        gameInfoBean.doReloadGame();

        verifyNoInteractions(gameService);

        gameInfoBean.setGame(game);
        gameInfoBean.doReloadGame();
        verify(gameService).loadGame(gameId);
    }
}
