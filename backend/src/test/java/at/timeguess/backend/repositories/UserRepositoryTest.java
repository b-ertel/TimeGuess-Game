package at.timeguess.backend.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static at.timeguess.backend.utils.TestSetup.*;
import static at.timeguess.backend.utils.TestUtils.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;

@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 })
    public void testFindByIdExisting(final Long userId) {
        assertFindById(userId, true);
    }

    @ParameterizedTest
    @ValueSource(longs = { 111, 222, 333, 444, 555, 666, 777, 888, 999 })
    public void testFindByIdNotExisting(final Long userId) {
        assertFindById(userId, false);
    }

    @ParameterizedTest
    @ValueSource(strings = { "admin", "user1", "user2" })
    public void testFindByUsernameExisting(final String username) {
        assertFindByUsername(username, true);
    }

    @ParameterizedTest
    @ValueSource(strings = { "foo", "bar", "bat" })
    public void testFindByUsernameeNotExisting(final String username) {
        assertFindByUsername(username, false);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|admin;elvis", "2|admin;user1;elvis;manger", "3|admin;user1;user2;michael;felix;lorenz;verena;claudia;clemens" })
    public void testFindByRoleExisting(final Integer userRole, final String usernamesExpected) {
        UserRole role = USERROLES.get(userRole);
        assertResultList(userRepository.findByRole(role),
            String.format("found users roles should contain %s but doesn't", role), true, u -> u.getRoles().contains(role));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|user1;user2;michael;felix;lorenz;verena;claudia;clemens;manger", "2|user2;michael;felix;lorenz;verena;claudia;clemens", "3|elvis;manger" })
    public void testFindByRoleNotExisting(final Integer userRole, final String usernamesExpected) {
        UserRole role = USERROLES.get(userRole);
        assertResultList(userRepository.findByRole(role),
            String.format("found users roles should not contain %s but does", role), true, u -> !usernamesExpected.contains(u.getUsername()));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|", "user1|user2;clemens", "user2|user1;claudia", "michael|felix;lorenz", "felix|michael;lorenz;clemens",
        "lorenz|michael;felix;verena", "verena|lorenz;claudia", "claudia|user2;verena;clemens", "clemens|user1;felix;claudia" })
    public void testFindByTeams(final String username, final String usernamesExpected) {
        User user = assertLoadUser(username);
        assertResultList(userRepository.findByTeams(user), "found user list should not contain %s but does",
            usernamesExpected != null && usernamesExpected.length() > 0, u -> usernamesExpected.contains(u.getUsername()));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|", "user1|7", "user2|8", "elvis|", "michael|1;4;6", "felix|1;3;6", "lorenz|1;4;5", "verena|2;5", "claudia|2;8", "clemens|3;7" })
    public void testFindAllTeamsIn(final String username, final String teamsExpected) {
        User user = assertLoadUser(username);
        Set<Long> teams = teamsExpected == null ? new HashSet<>()
                : Stream.of(teamsExpected.split(";")).map(Long::parseLong).collect(Collectors.toSet());
        List<Long> results = userRepository.findAllTeamsIn(user, teams);

        assertNotNull(results, "resulting team id list must not be null");
        assertTrue(results.containsAll(teams) && teams.containsAll(results), "input and output team ids should correspond, but don't");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|0", "user1|1", "user2|1", "elvis|0", "michael|3", "felix|3", "lorenz|3", "verena|2", "claudia|2", "clemens|2" })
    public void testGetTotalGames(final String username, final Integer totalExpected) {
        User user = assertLoadUser(username);
        int result = userRepository.getTotalGames(user);

        assertEquals(totalExpected, result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "admin", "user1", "user2", "elvis", "michael", "felix", "lorenz", "verena", "claudia", "clemens" })
    public void testGetByUsernameExisting(final String username) {
        int result = userRepository.getTotalByUsername(username);
        assertEquals(1, result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "foo", "bar", "bat" })
    public void testGetByUsernameNotExisting(final String username) {
        int result = userRepository.getTotalByUsername(username);
        assertEquals(0, result);
    }

    private User assertLoadUser(String username) {
        User user = userRepository.findFirstByUsername(username);
        assertEquals(username, user.getUsername(), "Call to userRepository.findFirstByUsername returned wrong user");
        return user;
    }

    private void assertFindById(Long userId, boolean expectExists) {
        Optional<User> result = userRepository.findById(userId);
        if (expectExists) {
            assertTrue(result.isPresent(), String.format("user with id=%d was not found", userId));
            assertEquals(userId, result.get().getId(), "found user has wrong id");
        }
        else {
            assertTrue(result.isEmpty(), String.format("user with id=%d was found", userId));
        }
    }

    private void assertFindByUsername(String username, boolean expectExists) {
        User result = userRepository.findFirstByUsername(username);
        if (expectExists) {
            assertNotNull(result, String.format("user with username %s was not found", username));
            assertEquals(username, result.getUsername(), "found user has wrong username");
        }
        else {
            assertNull(result, String.format("user with username %s was found", username));
        }
    }
}
