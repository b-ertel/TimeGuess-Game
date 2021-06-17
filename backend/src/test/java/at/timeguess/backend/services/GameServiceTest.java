package at.timeguess.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static at.timeguess.backend.utils.TestSetup.*;
import static at.timeguess.backend.utils.TestUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Game;
import at.timeguess.backend.model.GameState;
import at.timeguess.backend.model.Round;
import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.Topic;
import at.timeguess.backend.model.User;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link GameService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDatainitialization() {
        assertTrue(gameService.getAllGames().size() >= 5, "Insufficient amount of games initialized for test data source");
        for (Game game : gameService.getAllGames()) {
            if ("Game 1".equals(game.getName())) {
                assertTrue(game.getMaxPoints() == 40, "Game '" + game + "' does not have 45 maxPoints (it has" + game.getMaxPoints() + ").");
                assertTrue(game.getStatus() == GameState.FINISHED, "Game '" + game + "' is not finished");
                assertTrue(game.getTopic().getId() == 1, "Game '" + game + "' has wrong topic");
            }
        }
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 4, 5, 6, 7 })
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void canFindGame(long gameId) {
        assertLoadGame(gameId, true, "Game '%s' could not be loaded from test data source");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testDeleteGame() {
        long id_to_del = 3;
        int ctBefore = gameService.getAllGames().size();
        Game gameToDelete = assertLoadGame(id_to_del, true, "Game '%s' could not be loaded from test data source");

        gameService.deleteGame(gameToDelete);

        assertEquals(ctBefore - 1, gameService.getAllGames().size(), "No game has been deleted after calling gameService.deleteGame");
        assertNull(gameService.loadGame(id_to_del));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteFailure() {
        assertDoesNotThrow(() -> {
            gameService.deleteGame(null);
        }, "Deletion failure should not throw an exception, but does");
    }

    @Test
    @DirtiesContext
    @WithAnonymousUser
    public void testDeleteGameUnauthorized() {
        assertThrows(AccessDeniedException.class, () -> {
            Game game = assertLoadGame(1L, true, "Game '%s' could not be loaded from test data source");
            gameService.deleteGame(game);
        }, "Anonymous user should not be able to delete a game, but was");
        assertLoadGame(1L, true, "Game '%s' could not be loaded from test data source");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testUpdateGame() {
        long id = 4;
        Game game = assertLoadGame(id, true, "Game '%s' could not be loaded from test data source");

        // Simple properties
        String origName = game.getName();
        assertEquals(game.getName(), "Game 4", "Name not equal 'Game 4'");
        String newName = "My fantastic game";
        game.setName(newName);

        int origPoints = game.getMaxPoints();
        int newPoints = 100;
        game.setMaxPoints(newPoints);

        int origRound = game.getRoundNr();
        int newRound = 10;
        game.setRoundNr(newRound);

        // NOTE will need update in future if setStatus implements confirmation logic
        GameState oldStatus = game.getStatus();
        GameState newStatus = GameState.VALID_SETUP;
        game.setStatus(newStatus);

        // complex properties
        Topic oldTopic = game.getTopic();
        Topic newTopic = createTopic(1L);// topicService.loadTopicId(1L);

        game.setTopic(newTopic);

        User oldCreator = game.getCreator();
        User newCreator = userService.loadUser("user2");
        assertNotEquals(oldCreator, newCreator, "Game user is same as user2");
        game.setCreator(newCreator);

        gameService.saveGame(game);

        Game saveGame = assertLoadGame(id, true, "Game '%s' could not be reloaded from test data source");

        assertNotEquals(saveGame.getName(), origName, "Game still has original name");
        assertEquals(saveGame.getName(), newName, "Game name was not updated");

        assertNotEquals(saveGame.getMaxPoints(), origPoints, "Game still has original maxPoints");
        assertEquals(saveGame.getMaxPoints(), newPoints, "Game's maxPoints was not updated");

        assertNotEquals(saveGame.getRoundNr(), origRound, "Game still has original RoundNr");
        assertEquals(saveGame.getRoundNr(), newRound, "Game's RoundNr was not updated");

        assertNotEquals(saveGame.getStatus(), oldStatus, "Game still has original Status");
        assertEquals(saveGame.getStatus(), newStatus, "Game's Status was not updated");

        assertNotEquals(saveGame.getTopic(), oldTopic, "Game still has original Topic");
        assertEquals(saveGame.getTopic(), newTopic, "Game's Topic was not updated");

        assertNotEquals(saveGame.getCreator(), oldCreator, "Game still has original Creator");
        assertEquals(saveGame.getCreator(), newCreator, "Game's Creator was not updated");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testUpdateGameRound() {
        long id = 4;
        Game game = assertLoadGame(id, true, "Game '%s' could not be loaded from test data source");
        List<Round> rounds = new ArrayList<>(game.getRounds());

        assertTrue(rounds.size() > 0, "there should be rounds");

        int roundSizeBefore = rounds.size();

        // quickly find an element to remove
        rounds.remove(0);
        game.setRounds(new HashSet<>(rounds));

        gameService.saveGame(game);

        Game saveGame = assertLoadGame(id, true, "Game '%s' could not be loaded from test data source");
        int roundSizeAfter = saveGame.getRounds().size();

        assertEquals(roundSizeBefore - 1, roundSizeAfter, "Should be one less");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testUpdateGameTeams() {
        long id = 4;
        Game game = assertLoadGame(id, true, "Game '%s' could not be loaded from test data source");
        Set<Team> teams = game.getTeams();

        assertTrue(teams.size() > 0, "there should be teams");

        int teamSizeBefore = teams.size();

        // quickly find an element to remove
        List<Team> rl = teams.stream().collect(Collectors.toList());
        teams.remove(rl.get(0));

        // NOTE need to explicitly setTeams
        game.setTeams(teams);
        gameService.saveGame(game);

        Game saveGame = assertLoadGame(id, true, "Game '%s' could not be loaded from test data source");
        int teamSizeAfter = saveGame.getTeams().size();

        assertEquals(teamSizeBefore - 1, teamSizeAfter, "Should be one less");
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testSaveGame() {
        Game game = new Game();
        game.setName("TestGame1");
        assertNull(game.getId());

        game.setId(0L);
        assertTrue(game.isNew());
        game.setId(null);
        assertTrue(game.isNew());

        Game g0 = gameService.saveGame(game);
        assertNotNull(g0.getId());

        g0.setName("TestGame2");
        g0.setMaxPoints(10);
        Game g1 = gameService.saveGame(game);
        assertEquals(g1.getName(), "TestGame2");
        assertEquals(g1.getMaxPoints(), 10);

        // test other properties
        game.setConfirmedUsers(Set.of(createUser(1L)));
        assertNotNull(game.getConfirmedUsers());
        game.setConfirmedUsers(null);
        assertNotNull(game.getConfirmedUsers());

        Set<Team> teams = Set.of(createTeam(1L));
        game.setTeams(teams);
        assertEquals(teams, game.getTeams());
        assertEquals(1, game.getTeamCount());
        game.setTeams(null);
        assertNull(game.getTeams());
        assertEquals(0, game.getTeamCount());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveGameWithEmptyName() {
        assertDoesNotThrow(() -> {
            Game toBeCreatedGame = new Game();
            gameService.saveGame(toBeCreatedGame);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveGameWithExistingName() {
        assertDoesNotThrow(() -> {
            Game game = assertLoadGame(1, true, "Game '%s' could not be loaded from test data source");
            game.setName("Game 2");
            gameService.saveGame(game);
        });
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testConfirm() {
        Game game6 = assertLoadGame(6L, true, "Game '%s' could not be loaded from test data source");

        // game in SETUP state, invited user
        User user3 = userService.loadUser(createUser(3L));
        assertDoesNotThrow(() -> gameService.confirm(user3, game6));
        assertTrue(game6.getConfirmedUsers().contains(user3), "User with id=3 is expected to be in game with id=6, but isn't");

        // game in SETUP state, not invited user
        User user1 = userService.loadUser(createUser(1L));
        assertDoesNotThrow(() -> gameService.confirm(user1, game6));
        assertFalse(game6.getConfirmedUsers().contains(user1), "User with id=1 is not expected to be in game with id=6, but is");

        // game in FINISHED state
        Game game1 = assertLoadGame(1L, true, "Game '%s' could not be loaded from test data source");
        assertDoesNotThrow(() -> gameService.confirm(user3, game1));
        assertFalse(game1.getConfirmedUsers().contains(user1), "User with id=1 is not expected to be in game with id=1, but is");

        // game in VALID_SETUP state, user already confirmed
        game6.setStatus(GameState.VALID_SETUP);
        assertDoesNotThrow(() -> gameService.confirm(user3, game6));
        assertTrue(game6.getConfirmedUsers().contains(user3), "User with id=3 is expected to be in game with id=6, but isn't");
    }

    @Test
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetAllCurrent() {
        List<Game> expected = createEntities(TestSetup::createGame, Arrays.asList(5L, 6L, 7L, 8L, 9L));
        List<Game> result = gameService.getAllCurrent();

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "0|6;7", "1|", "2|5;8;9", "3|", "4|1;2;3;4", "5|" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetByStatus(int gameState, final String gameIdsExpected) {
        List<Game> expected = createEntities(TestSetup::createGame, gameIdsExpected);
        List<Game> result = gameService.getByStatus(GAMESTATES.get(gameState));

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "0;1|6;7", "0;1;2|5;6;7;8;9", "2;3;4|1;2;3;4;5;8;9", "3|", "4|1;2;3;4", "3;5|" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetByStatus(String gameStates, final String gameIdsExpected) {
        List<Game> expected = createEntities(TestSetup::createGame, gameIdsExpected);
        List<Game> result = gameService
            .getByStatus(createEntities(gs -> GAMESTATES.get(gs.intValue()), gameStates).toArray(GameState[]::new));

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|", "2|4;5;9", "3|4;6;9", "4|", "5|1;2;3;8", "6|1;2;3;6;8", "7|1;2;3;5;8", "8|1;3;5;8", "9|1;4;6;9", "10|2;4;5;6;9" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetByUserAll(long userId, String gamesIdsExpected) {
        List<Game> expected = createEntities(TestSetup::createGame, gamesIdsExpected);
        List<Game> result = gameService.getByUser(createUser(userId), false);

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|", "2|5;9", "3|6;9", "4|", "5|8", "6|6;8", "7|5;8", "8|5;8", "9|6;9", "10|5;6;9" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetByUserCurrent(long userId, String gamesIdsExpected) {
        List<Game> expected = createEntities(TestSetup::createGame, gamesIdsExpected);
        List<Game> result = gameService.getByUser(createUser(userId), true);

        assertLists(expected, result);
    }

    private Game assertLoadGame(long gameId, boolean shouldExist, String msgFormat) {
        Game game = gameService.loadGame(gameId);
        if (shouldExist) assertNotNull(game, String.format(msgFormat, String.valueOf(gameId)));
        else assertNull(game, String.format(msgFormat, game));
        return game;
    }
}
