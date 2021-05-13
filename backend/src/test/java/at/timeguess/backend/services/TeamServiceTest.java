package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.*;
import static at.timeguess.backend.utils.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.Team;
import at.timeguess.backend.model.User;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link TeamService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 4, 5, 6, 7, 8 })
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testFindById(long teamId) {
        assertLoadTeam(teamId, true, "Team '%s' could not be loaded from test data source");
    }

    @ParameterizedTest
    @ValueSource(strings = { "DIE GERECHTIGKEITSLIGA", "THE FELLOWSHIP OF THE TERM", "Team 4", "Team 5", "Team 6", "Team 7", "Team 8" })
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testLoadTeam(String teamName) {
        assertLoadTeam(teamName, true, "Team '%s' could not be loaded from test data source");
    }

    @Test
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetAllTeams() {
        List<Team> expected = createEntities(TestSetup::createTeam, LongStream.rangeClosed(1, 12).boxed());
        List<Team> result = teamService.getAllTeams();

        assertLists(expected, result);

        expected = createEntities(id -> teamService.findById(id).get(), LongStream.rangeClosed(1, 12).boxed());
        assertListsCompare(expected, result);
    }

    @Test
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetAvailableTeams() {
        List<Team> expected = createEntities(TestSetup::createTeam, LongStream.of(1, 2, 4).boxed());
        List<Team> result = teamService.getAvailableTeams();

        assertLists(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|true", "2|true", "3|false", "4|true", "5|false", "6|false", "7|false",
            "8|false" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testIsAvailableTeam(long teamId, boolean expected) {
        assertEquals(expected, teamService.isAvailableTeam(createTeam(teamId)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "1|1|true",  "1|2|true",  "1|3|true",  "1|4|true",  "1|5|true",  "1|6|true",  "1|7|true",
            "2|1|true",  "2|2|true",  "2|3|true",  "2|4|true",  "2|5|true",  "2|6|true",  "2|7|true",
            "3|1|false", "3|2|false", "3|3|false", "3|4|false", "3|5|false", "3|6|true",  "3|7|false",
            "4|1|true",  "4|2|true",  "4|3|true",  "4|4|true",  "4|5|true",  "4|6|true",  "4|7|true",
            "5|1|false", "5|2|false", "5|3|false", "5|4|false", "5|5|true",  "5|6|false", "5|7|false",
            "6|1|false", "6|2|false", "6|3|false", "6|4|false", "6|5|false", "6|6|false", "6|7|false",
            "7|1|false", "7|2|false", "7|3|false", "7|4|false", "7|5|true",  "7|6|false", "7|7|false",
            "8|1|false", "8|2|false", "8|3|false", "8|4|false", "8|5|false", "8|6|true",  "8|7|false"
    })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testIsAvailableTeamForGame(long teamId, long gameId, boolean expected) {
        assertEquals(expected, teamService.isAvailableTeamForGame(createTeam(teamId), createGame(gameId)));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN", "MANAGER" })
    public void testSaveTeam() {
        Team team = new Team();
        team.setName("TestTeam1");
        assertNull(team.getId());

        team.setId(0L);
        assertTrue(team.isNew());
        team.setId(null);
        assertTrue(team.isNew());

        Team t0 = teamService.saveTeam(team);
        assertNotNull(t0.getId());

        t0.setName("TestTeam2");
        Team t1 = teamService.saveTeam(team);
        assertEquals(t1.getName(), "TestTeam2");
        assertEquals(t0.toString(), t1.toString());

        // test other properties
        team.setGames(Set.of(createGame(1L)));
        assertNotNull(team.getGames());
        team.setGames(null);
        assertNull(team.getGames());

        Set<User> users = Set.of(createUser(1L));
        team.setTeamMembers(users);
        assertEquals(users, team.getTeamMembers());
        team.setTeamMembers(null);
        assertNull(team.getTeamMembers());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveTeamWithEmptyName() {
        assertDoesNotThrow(() -> {
            Team toBeCreatedTeam = new Team();
            teamService.saveTeam(toBeCreatedTeam);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveTeamWithExistingName() {
        assertDoesNotThrow(() -> {
            Team team = assertLoadTeam("Team 4", true, "Team '%s' could not be loaded from test data source");
            team.setName("Team 5");
            teamService.saveTeam(team);
        });
    }

    private Team assertLoadTeam(long teamId, boolean shouldExist, String msgFormat) {
        Team team = teamService.findById(teamId).orElse(null);
        if (shouldExist) assertNotNull(team, String.format(msgFormat, String.valueOf(teamId)));
        else assertNull(team, String.format(msgFormat, team));
        return team;
    }

    private Team assertLoadTeam(String teamName, boolean shouldExist, String msgFormat) {
        Team team = teamService.loadTeam(teamName);
        if (shouldExist) assertNotNull(team, String.format(msgFormat, teamName));
        else assertNull(team, String.format(msgFormat, teamName));
        return team;
    }
}
