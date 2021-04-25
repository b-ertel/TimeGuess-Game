package at.timeguess.backend.ui.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.User;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.utils.TestUtils;

/**
 * Tests for {@link UserProfileController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserService userService;

    @ParameterizedTest
    @ValueSource(longs = { 4, 5, 6, 89, 888 })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testDoReloadUser(Long userId) {
        assertMockUser(userId);

        userProfileController.doReloadUser();

        verify(userService, times(2)).loadUser(userId);
        assertEquals(userId, userProfileController.getUser().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 4, 5, 6, 89, 888 })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetTeammates(Long userId) {
        Collection<User> users = TestUtils.createUsers(10);
        User user = assertMockUser(userId);
        when(userService.getTeammates(user)).thenReturn(users);

        Collection<User> result = userProfileController.getTeammates();

        verify(userService).getTeammates(user);
        assertEquals(users, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 4, 5, 6, 89, 888 })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetTotalGames(Long userId) {
        int expected = 25;
        User user = assertMockUser(userId);
        when(userService.getTotalGames(user)).thenReturn(expected);

        int result = userProfileController.getTotalGames();

        verify(userService).getTotalGames(user);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 4, 5, 6, 89, 888 })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetTotalGamesLost(Long userId) {
        int expected = 9;
        User user = assertMockUser(userId);
        when(userService.getTotalGamesLost(user)).thenReturn(expected);

        int result = userProfileController.getTotalGamesLost();

        verify(userService).getTotalGamesLost(user);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @ValueSource(longs = { 4, 5, 6, 89, 888 })
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetTotalGamesWon(Long userId) {
        int expected = 16;
        User user = assertMockUser(userId);
        when(userService.getTotalGamesWon(user)).thenReturn(expected);

        int result = userProfileController.getTotalGamesWon();

        verify(userService).getTotalGamesWon(user);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @MethodSource("provideWonByTopic")
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetTotalGamesWonByTopic(String username, Map<String, Integer> totalsExpected) {
        User user = assertMockUser(TestUtils.createUser(username));
        when(userService.getTotalGamesWonByTopic(user)).thenReturn(totalsExpected);

        Map<String, Integer> result = userProfileController.getTotalGamesWonByTopic();

        verify(userService).getTotalGamesWonByTopic(user);
        assertEquals(totalsExpected, result);
    }

    private User assertMockUser(Long userId) {
        return assertMockUser(TestUtils.createUser(userId));
    }

    private User assertMockUser(User user) {
        when(userService.loadUser(user.getId())).thenReturn(user);

        userProfileController.setUser(user);

        verify(userService).loadUser(user.getId());
        assertEquals(user.getId(), userProfileController.getUser().getId());
        return user;
    }

    private static Stream<Arguments> provideWonByTopic() {
        return TestUtils.USER_TOTALWINSBYTOPIC.stream();
    }
}