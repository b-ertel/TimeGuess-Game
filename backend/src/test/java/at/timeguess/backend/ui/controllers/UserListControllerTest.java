package at.timeguess.backend.ui.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
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
 * Tests for {@link UserListController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class UserListControllerTest {

    @InjectMocks
    private UserListController userListController;

    @Mock
    private UserService userService;

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetUsers() {
        List<User> users = TestUtils.createEntities(TestUtils::createUser, 10);
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userListController.getUsers();

        verify(userService).getAllUsers();
        assertEquals(users, result);
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetAllPlayers() {
        List<User> users = TestUtils.createEntities(TestUtils::createUser, 10);
        when(userService.getAllPlayers()).thenReturn(users);

        List<User> result = userListController.getAllPlayers();

        verify(userService).getAllPlayers();
        assertEquals(users, result);
    }
}
