package at.timeguess.backend.ui.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static at.timeguess.backend.utils.TestSetup.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.UserService;
import at.timeguess.backend.ui.beans.MessageBean;

/**
 * Tests for {@link UserDetailController}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class UserDetailControllerTest {

    @InjectMocks
    private UserDetailController userDetailController;

    @Mock
    private UserService userService;
    @Mock
    private MessageBean messageBean;
    @Mock
    private PasswordEncoder passwordEncoder;

    @ParameterizedTest
    @ValueSource(longs = { 1, 40, 444 })
    public void testGetSetUserRoles(Long userId) {
        Collection<UserRole> allroles = userDetailController.getAllUserRoles();
        assertEquals(allroles, UserRole.getUserRoles());

        assertMockUser(userId, false);

        List<UserRole> minroles = List.of(UserRole.PLAYER);
        userDetailController.setUserRoles(minroles);
        assertEquals(minroles, userDetailController.getUserRoles());

        User user = userDetailController.getUser();
        assertTrue(user.getRoles().containsAll(userDetailController.getUserRoles()));
        assertTrue(userDetailController.getUserRoles().containsAll(user.getRoles()));

        userDetailController.doDeleteUser();
        assertDoesNotThrow(() -> userDetailController.setUserRoles(minroles));
        assertNull(userDetailController.getUserRoles());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 40, 444 })
    public void testDoReloadUser(Long userId) {
        User user = assertMockUser(userId, false);

        userDetailController.doReloadUser();

        verify(userService, times(2)).loadUser(user);
        assertEquals(userId, userDetailController.getUser().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 40, 444 })
    public void testDoSaveUser(Long userId) {
        User user = assertMockUser(userId, true);
        when(userService.saveUser(user)).thenReturn(user);

        userDetailController.doSaveUser();

        verify(userService).saveUser(user);
        verifyNoInteractions(passwordEncoder);
        assertEquals(userId, userDetailController.getUser().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1,  40, 444 })
    public void testDoSaveUserChangedPassword(Long userId) {
        String changed = "changed", encoded = "encoded";
        User user = assertMockUser(userId, true);
        when(userService.saveUser(user)).thenReturn(user);
        when(passwordEncoder.encode(changed)).thenReturn(encoded);

        user.setPassword(changed);
        userDetailController.doSaveUser();

        verify(userService).saveUser(user);
        verify(passwordEncoder).encode(anyString());
        verify(messageBean).alertInformation(anyString(), anyString());
        assertEquals(encoded, user.getPassword());
        assertEquals(userId, userDetailController.getUser().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 11, 22 })
    public void testDoSaveTermFailure(Long termId) {
        User user = assertMockUser(termId, true);
        when(userService.saveUser(user)).thenReturn(null);

        userDetailController.doSaveUser();

        verify(userService).saveUser(user);
        verifyNoInteractions(messageBean);
        assertEquals(user, userDetailController.getUser());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 40, 444 })
    public void testDoSaveUserInvalid(Long userId) {
        assertMockUser(userId, false);
        reset(userService);

        userDetailController.doSaveUser();

        verifyNoInteractions(userService);
        verifyNoInteractions(passwordEncoder);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
        assertEquals(userId, userDetailController.getUser().getId());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 40, 444 })
    public void testDoDeleteUser(Long userId) {
        User user = assertMockUser(userId, false);

        userDetailController.doDeleteUser();

        verify(userService).deleteUser(user);
        assertNull(userDetailController.getUser());
    }

    @ParameterizedTest
    @ValueSource(longs = { 1, 2, 3, 40, 444 })
    public void testDoValidateUser(Long userId) {
        User user = assertMockUser(userId, false);

        assertFalse(userDetailController.doValidateUser());
        user.setUsername("uname");
        assertFalse(userDetailController.doValidateUser());
        user.setPassword("pword");
        assertFalse(userDetailController.doValidateUser());
        user.setRoles(null);
        assertFalse(userDetailController.doValidateUser());
        user.setRoles(userDetailController.getAllUserRoles());
        assertTrue(userDetailController.doValidateUser());
    }

    private User assertMockUser(Long userId, boolean full) {
        User user = createUser(userId);
        if (full) {
            user.setUsername("uname");
            user.setPassword("pword");
            user.setRoles(UserRole.mapUserRole(UserRole.PLAYER));
        }
        when(userService.loadUser(user)).thenReturn(user);

        userDetailController.setUser(user);

        verify(userService).loadUser(user);
        assertEquals(userId, userDetailController.getUser().getId());
        return user;
    }
}
