package at.timeguess.backend.services;

import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omnifaces.cdi.PushContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.User;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;

/**
 * Some mock tests for {@link UserService}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class UserServiceMockedTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageBean messageBean;
    @Mock
    private WebSocketManager websocketManager;

    @Test
    public void testSaveUser() {
        assertSaveUser("aname", true);
    }

    @Test
    public void testSaveUserAnonymous() {
        assertSaveUser("anonymous", false);
    }

    @Test
    public void testSaveUserInternal() {
        assertSaveUser("aname", false);
    }

    @Test
    public void testDeleteUser() {
        User user = createUser(5L);
        user.setUsername("aname");
        PushContext context = mock(PushContext.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);

        assertDoesNotThrow(() -> userService.deleteUser(user));

        verify(userRepository).delete(user);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(context).send(anyMap());
    }

    @Test
    public void testDeleteUserFailure() {
        User user = createUser(5L);
        user.setUsername("aname");
        PushContext context = mock(PushContext.class);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(context);
        when(userRepository.getTotalByUsername(anyString())).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUser(user));

        verify(userRepository).delete(user);
        verify(userRepository).getTotalByUsername(anyString());
        verify(messageBean).alertErrorFailValidation(nullable(String.class), anyString());
        verifyNoMoreInteractions(context);
    }

    private void assertSaveUser(String username, boolean known) {
        User user = createUser(5L);
        user.setUsername(username);

        SecurityContext contextSec = mock(SecurityContext.class);
        SecurityContextHolder.setContext(contextSec);
        Authentication auth = mock(Authentication.class);
        when(contextSec.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn(username);
        if (known)
            when(userRepository.findFirstByUsername(username)).thenReturn(user);
        else
            when(userRepository.findFirstByUsername(username)).thenReturn(null);

        PushContext contextPush = mock(PushContext.class);
        when(userRepository.save(user)).thenReturn(user);
        when(websocketManager.getUserRegistrationChannel()).thenReturn(contextPush);

        assertDoesNotThrow(() -> userService.saveUser(user));

        verify(userRepository).save(user);
        verify(messageBean).alertInformation(nullable(String.class), anyString());
        verify(contextPush).send(anyMap());
    }
}
