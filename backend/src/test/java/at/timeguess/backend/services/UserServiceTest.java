package at.timeguess.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static at.timeguess.backend.utils.TestSetup.*;
import static at.timeguess.backend.utils.TestUtils.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.utils.TestSetup;

/**
 * Tests for {@link UserService}.
 */
@SpringBootTest
@WebAppConfiguration
@Sql({ "classpath:deleteAll.sql", "classpath:dataTest.sql" })
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|1", "user1|2", "user2|3", "elvis|1", "michael|3", "felix|3", "lorenz|3", "verena|3", "claudia|3", "clemens|3" })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDatainitialization(String username, Integer roleExpected) {
        User user = assertLoadUser(username, true, "User '%s' could not be loaded");

        assertTrue(user.getRoles().contains(USERROLES.get(roleExpected)), String.format("User '%s' does not have role %s", user, USERROLES.get(roleExpected).toString()));
        assertNotNull(user.getCreateUser(), String.format("User '%s' does not have a createUser defined", user));
        assertNotNull(user.getCreateDate(), String.format("User '%s' does not have a createDate defined", user));
        assertNull(user.getUpdateUser(), String.format("User '%s' has a updateUser defined", user));
        assertNull(user.getUpdateDate(), String.format("User '%s' has a updateDate defined", user));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "elvis|admin", "manger|elvis" })
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteUser(String username, String adminUsername) {
        assertLoadUser(adminUsername, true, "Admin user '%s' could not be loaded from test data source");

        User toBeDeletedUser = assertLoadUser(username, true, "User '%s' could not be loaded from test data source");
        int ctBefore = userService.getAllUsers().size();

        userService.deleteUser(toBeDeletedUser);

        assertEquals(ctBefore - 1, userService.getAllUsers().size(), "No user has been deleted after calling UserService.deleteUser");
        assertLoadUser(username, false, "Deleted User '%s' could still be loaded from test data source via UserService.loadUser");

        for (User remainingUser : userService.getAllUsers()) {
            assertNotEquals(toBeDeletedUser.getUsername(), remainingUser.getUsername(),
                String.format("Deleted User '%s' could still be loaded from test data source via UserService.getAllUsers", username));
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "user1|admin", "user2|admin" })
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" }, setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testUpdateUser(String username, String adminUsername) {
        User adminUser = assertLoadUser(adminUsername, true, "Admin user '%s' could not be loaded from test data source");
        User toBeSavedUser = assertLoadUser(username, true, "User '%s' could not be loaded from test data source");

        assertNull(toBeSavedUser.getUpdateUser(), String.format("User '%s' has a updateUser defined", username));
        assertNull(toBeSavedUser.getUpdateDate(), String.format("User '%s' has a updateDate defined", username));

        toBeSavedUser.setEmail("changed@who.why");
        userService.saveUser(toBeSavedUser);

        User freshlyLoadedUser = assertLoadUser(username, true, "User '%s' could not be loaded from test data source after being saved");
        assertNotNull(freshlyLoadedUser.getUpdateUser(), String.format("User '%s' does not have a updateUser defined after being saved", username));
        assertEquals(adminUser, freshlyLoadedUser.getUpdateUser(), String.format("User '%s' has wrong updateUser set", username));
        assertNotNull(freshlyLoadedUser.getUpdateDate(), String.format("User '%s' does not have a updateDate defined after being saved", username));
        assertEquals("changed@who.why", freshlyLoadedUser.getEmail(), String.format("User '%s' does not have a the correct email attribute stored being saved", username));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testCreateUser() {
        User adminUser = assertLoadUser("admin", true, "Admin user '%s' could not be loaded from test data source");

        String username = "newuser";
        String password = "passwd";
        String fName = "New";
        String lName = "User";
        String email = "new@who.why";
        User toBeCreatedUser = new User();
        toBeCreatedUser.setUsername(username);
        toBeCreatedUser.setPassword(password);
        toBeCreatedUser.setEnabled(true);
        toBeCreatedUser.setFirstName(fName);
        toBeCreatedUser.setLastName(lName);
        toBeCreatedUser.setEmail(email);
        toBeCreatedUser.setRoles(Set.of(UserRole.PLAYER, UserRole.MANAGER));

        toBeCreatedUser.setId(0L);
        assertTrue(toBeCreatedUser.isNew());
        toBeCreatedUser.setId(null);
        assertTrue(toBeCreatedUser.isNew());

        userService.saveUser(toBeCreatedUser);

        User freshlyCreatedUser = assertLoadUser(username, true, "New user '%s' could not be loaded from test data source after being saved");
        assertEquals(username, freshlyCreatedUser.getUsername(), "User '%s' does not have a the correct username attribute stored being saved");
        assertEquals(password, freshlyCreatedUser.getPassword(), String.format("User '%s' does not have a the correct password attribute stored being saved", username));
        assertEquals(true, freshlyCreatedUser.isEnabled(), String.format("User '%s' does not have a the correct activation state stored being saved", username));
        assertEquals(fName, freshlyCreatedUser.getFirstName(), String.format("User '%s' does not have a the correct firstName attribute stored being saved", username));
        assertEquals(lName, freshlyCreatedUser.getLastName(), String.format("User '%s' does not have a the correct lastName attribute stored being saved", username));
        assertEquals(email, freshlyCreatedUser.getEmail(), String.format("User '%s' does not have a the correct email attribute stored being saved", username));
        assertTrue(freshlyCreatedUser.getRoles().contains(UserRole.MANAGER), String.format("User '%s' does not have role MANAGER", username));
        assertTrue(freshlyCreatedUser.getRoles().contains(UserRole.PLAYER), String.format("User '%s' does not have role PLAYER", username));
        assertNotNull(freshlyCreatedUser.getCreateUser(), String.format("User '%s' does not have a createUser defined after being saved", username));
        assertEquals(adminUser, freshlyCreatedUser.getCreateUser(), String.format("User '%s' has wrong createUser set", username));
        assertNotNull(freshlyCreatedUser.getCreateDate(), String.format("User '%s' does not have a createDate defined after being saved", username));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveUserWithEmptyUsername() {
        assertDoesNotThrow(() -> {
            User toBeCreatedUser = new User();
            userService.saveUser(toBeCreatedUser);
        });
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    @DirtiesContext
    public void testSaveUserWithExistingUsername() {
        assertDoesNotThrow(() -> {
            User user = assertLoadUser("admin", true, "Admin user '%s' could not be loaded from test data source");
            user.setUsername("user1");
            userService.saveUser(user);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "user2" })
    @DirtiesContext
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testSaveUserAuthorized(String username) {
        assertDoesNotThrow(() -> {
            User user = assertLoadUser(username);
            user.setEmail("email");
            userService.saveUser(user);
        }, "User should be able to change his own data, but wasn't");

        User user = assertLoadUser(username);
        assertEquals("email", user.getEmail());
    }

    @ParameterizedTest
    @ValueSource(strings = { "user2" })
    @DirtiesContext
    @WithMockUser(username = "user1", authorities = { "PLAYER" })
    public void testSaveUserUnauthorizedFailure(String username) {
        assertThrows(AccessDeniedException.class, () -> {
            User user = assertLoadUser(username);
            userService.saveUser(user);
        }, "PLAYER should not be able to save another user, but was");
    }

    @ParameterizedTest
    @ValueSource(strings = { "foo", "bar", "bat" })
    @DirtiesContext
    @WithAnonymousUser
    public void testSaveUserUnauthorizedSuccess(String username) {
        assertDoesNotThrow(() -> {
            User expected = createUser(username);
            expected.setPassword("pword");

            User result = userService.saveUser(expected);
            assertEquals(expected.getUsername(), result.getUsername());
        }, "Anonymous user should be able to self-register, but wasn't");
    }

    @Test
    public void testLoadUsersUnauthenticated() {
        assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
            for (User user : userService.getAllUsers()) {
                fail(String.format("Call to userService.getAllUsers should not work without proper authorization, but found %s", user));
            }
        });
    }

    @Test
    @WithMockUser(username = "user", authorities = { "PLAYER" })
    public void testLoadUsersUnauthorized() {
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () -> {
            for (User user : userService.getAllUsers()) {
                fail(String.format("Call to userService.getAllUsers should not work without proper authorization, but found %s", user));
            }
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "admin", "user1", "user2" })
    @WithMockUser(username = "user1", authorities = { "MANAGER" })
    public void testLoadUserUnauthorized(String username) {
        assertThrows(AccessDeniedException.class, () -> {
            User user = userService.loadUser("admin");
            fail(String.format("Call to userService.loadUser should not work without proper authorization for other users than the authenticated one, but found %s", user));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = { "admin", "user1", "user2" })
    @WithMockUser(username = "user1", authorities = { "PLAYER" })
    public void testLoadUserAuthorized(String username) {
        assertLoadUser(username);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|admin", "2|user1", "3|user2", "4|elvis" })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testLoadUserByIdAuthorized(Long userId, String usernameExpected) {
        User user = userService.loadUser(createUser(userId));
        assertEquals(usernameExpected, user.getUsername());
    }

    @ParameterizedTest
    @ValueSource(strings = { "elvis" })
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteUserAuthorized(String username) {
        assertDoesNotThrow(() -> {
            User user = assertLoadUser(username);
            userService.deleteUser(user);
        }, "ADMIN should be able to delete another user, but wasn't");
        assertFalse(userService.hasUser(username));
    }

    @Test
    @DirtiesContext
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDeleteUserSelfAuthorized() throws NoSuchMethodException, SecurityException {
        String username = UserServiceTest.class.getMethod("testDeleteUserSelfAuthorized").getAnnotation(WithMockUser.class).username();
        assertDoesNotThrow(() -> {
            User user = assertLoadUser(username);
            userService.deleteUser(user);
        });
        assertTrue(userService.hasUser(username), "User should not be able to delete himself, but was");
    }

    @ParameterizedTest
    @ValueSource(strings = { "elvis" })
    @DirtiesContext
    @WithMockUser(username = "user1", authorities = { "PLAYER" })
    public void testDeleteUserUnauthorized(String username) {
        assertThrows(AccessDeniedException.class, () -> {
            User user = assertLoadUser(username);
            userService.deleteUser(user);
        }, "PLAYER should not be able to delete another user, but was");
        assertTrue(userService.hasUser(username));
    }

    @Test
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetAllPlayers() {
        List<User> expected = createEntities(TestSetup::createUser, Arrays.asList("admin", "user1", "user2", "michael", "felix", "lorenz", "verena", "claudia", "clemens"));
        List<User> result = userService.getAllPlayers();

        assertListsCompare(expected, result);
    }

    @Test
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetAvailablePlayers() {
        List<User> expected = createEntities(TestSetup::createUser, Arrays.asList("admin"));
        List<User> result = userService.getAvailablePlayers();

        assertListsCompare(expected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "1|true", "2|false", "3|false", "4|false", "5|false", "6|false", "7|false", "8|false", "9|false", "10|false", "11|false" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testIsAvailablePlayer(long userId, boolean expected) {
        assertEquals(expected, userService.isAvailablePlayer(createUser(userId)));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|", "user1|user2;clemens", "user2|user1;claudia", "michael|felix;lorenz", "felix|michael;lorenz;clemens",
        "lorenz|michael;felix;verena", "verena|lorenz;claudia", "claudia|user2;verena;clemens", "clemens|user1;felix;claudia" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetTeammates(String username, String usernamesExpected) {
        User user = assertLoadUser(username);
        assertResultList(userService.getTeammates(user), "found user list should not contain %s but does",
            usernamesExpected != null && usernamesExpected.length() > 0, u -> usernamesExpected.contains(u.getUsername()));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|0", "user1|1", "user2|1", "elvis|0", "michael|3", "felix|3", "lorenz|3", "verena|2", "claudia|2", "clemens|2" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetTotalGames(String username, Integer totalExpected) {
        User user = assertLoadUser(username);
        int result = userService.getTotalGames(user);

        assertEquals(totalExpected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|0", "user1|1", "user2|0", "elvis|0", "michael|1", "felix|2", "lorenz|2", "verena|1", "claudia|0", "clemens|2" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetTotalGamesLost(String username, Integer totalExpected) {
        User user = assertLoadUser(username);
        int result = userService.getTotalGamesLost(user);

        assertEquals(totalExpected, result);
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = { "admin|0", "user1|0", "user2|1", "elvis|0", "michael|2", "felix|1", "lorenz|1", "verena|1", "claudia|2", "clemens|0" })
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetTotalGamesWon(String username, Integer totalExpected) {
        User user = assertLoadUser(username);
        int result = userService.getTotalGamesWon(user);

        assertEquals(totalExpected, result);
    }

    @ParameterizedTest
    @MethodSource("provideWonByTopic")
    @WithMockUser(username = "user2", authorities = { "PLAYER" })
    public void testGetTotalGamesWonByTopic(String username, Map<String, Integer> totalsExpected) {
        User user = assertLoadUser(username);
        Map<String, Integer> result = userService.getTotalGamesWonByTopic(user);

        assertEquals(totalsExpected, result);
    }

    private User assertLoadUser(String username, boolean shouldExist, String msgFormat) {
        User user = userService.loadUser(username);
        if (shouldExist)
            assertNotNull(user, String.format(msgFormat, username));
        else
            assertNull(user, String.format(msgFormat, user));
        return user;
    }

    private User assertLoadUser(String username) {
        User user = userService.loadUser(username);
        assertEquals(username, user.getUsername(), "Call to userService.loadUser returned wrong user");
        return user;
    }

    private static Stream<Arguments> provideWonByTopic() {
        return USER_TOTALWINSBYTOPIC.stream();
    }
}
