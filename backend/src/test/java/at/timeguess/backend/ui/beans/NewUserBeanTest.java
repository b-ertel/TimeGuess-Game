package at.timeguess.backend.ui.beans;

import static at.timeguess.backend.utils.TestSetup.USERROLES;
import static at.timeguess.backend.utils.TestSetup.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

/**
 * Tests for {@link NewUserBean}.
 */
@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class NewUserBeanTest {

    @InjectMocks
    private NewUserBean newUserBean;

    @Mock
    private UserService userService;
    @Mock
    private MessageBean messageBean;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void beforeEach() {
        newUserBean.clearFields();
    }

    @Test
    public void testGetAllUserRoles() {
        Collection<UserRole> roles = newUserBean.getAllUserRoles();

        assertEquals(roles, UserRole.getUserRoles());
        assertTrue(roles.contains(UserRole.PLAYER));
        assertTrue(roles.contains(UserRole.MANAGER));
        assertTrue(roles.contains(UserRole.ADMIN));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    public void testClearFields(Integer roleNr) {
        UserRole role = USERROLES.get(roleNr);
        String foo = fillBean(true);
        newUserBean.setUserRole(role);
        newUserBean.setEnabled(false);

        assertFields(foo, true);
        assertEquals(role, newUserBean.getUserRole());
        assertFalse(newUserBean.isEnabled());

        newUserBean.clearFields();

        assertFieldsClear();
    }

    @Test
    public void testCreateUser() {
        String foo = fillBean(false);
        User expected = createUser(6L);
        when(userService.saveUser(any(User.class))).thenReturn(expected);

        User result = newUserBean.createUser();

        verify(userService).saveUser(any(User.class));
        verify(passwordEncoder).encode(foo);
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);
        assertFieldsClear();
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 3 })
    public void testCreateUserAdmin(Integer roleNr) {
        String foo = fillBean(true);
        User expected = createUser(6L);
        when(userService.saveUser(any(User.class))).thenReturn(expected);
        newUserBean.setUserRole(USERROLES.get(roleNr));

        User result = newUserBean.createUserAdmin();

        verify(userService).saveUser(any(User.class));
        verify(passwordEncoder).encode(foo);
        verifyNoInteractions(messageBean);
        assertEquals(expected, result);
        assertFieldsClear();
    }

    @Test
    public void testCreateUserFailure() {
        newUserBean.createUser();

        verifyNoInteractions(userService);
        verifyNoInteractions(passwordEncoder);
        verify(messageBean).alertErrorFailValidation(anyString(), anyString());
    }

    @Test
    public void testCreateUserFailureSave() {
        String foo = fillBean(false);
        when(userService.saveUser(any(User.class))).thenReturn(null);

        User result = newUserBean.createUser();

        verify(userService).saveUser(any(User.class));
        verifyNoInteractions(messageBean);
        assertNull(result);
        assertFields(foo, false);
    }

    @Test
    public void testValidateInput() {
        String foo = assertValidateCommon();

        newUserBean.setRepeated("rep");
        assertFalse(newUserBean.validateInput(false));
        newUserBean.setRepeated(foo);
        assertTrue(newUserBean.validateInput(false));
    }

    @Test
    public void testValidateInputAdmin() {
        String foo = assertValidateCommon();

        assertFalse(newUserBean.validateInput(true));
        newUserBean.setFirstName(foo);
        assertFalse(newUserBean.validateInput(true));
        newUserBean.setLastName(foo);
        assertFalse(newUserBean.validateInput(true));
        newUserBean.setEmail(foo);
        assertTrue(newUserBean.validateInput(true));
    }

    private String assertValidateCommon() {
        String foo = "foobarbat";

        when(userService.hasUser(foo)).thenReturn(true);
        assertFalse(newUserBean.validateInput(false));
        newUserBean.setUsername("");
        assertFalse(newUserBean.validateInput(false));
        newUserBean.setUsername(foo);
        assertFalse(newUserBean.validateInput(false));
        when(userService.hasUser(foo)).thenReturn(false);
        assertFalse(newUserBean.validateInput(false));

        newUserBean.setPassword(foo);
        assertFalse(newUserBean.validateInput(false));

        return foo;
    }

    private String fillBean(boolean isAdmin) {
        String foo = "foobarbat";

        newUserBean.setUsername(foo);
        newUserBean.setPassword(foo);
        newUserBean.setRepeated(foo);
        if (isAdmin) {
            newUserBean.setFirstName(foo);
            newUserBean.setLastName(foo);
            newUserBean.setEmail(foo);
            newUserBean.setEnabled(false);
        }
        return foo;
    }

    private void assertFields(String expected, boolean isAdmin) {
        assertEquals(expected, newUserBean.getUsername());
        assertEquals(expected, newUserBean.getPassword());
        assertEquals(expected, newUserBean.getRepeated());

        if (isAdmin) {
            assertEquals(expected, newUserBean.getFirstName());
            assertEquals(expected, newUserBean.getLastName());
            assertEquals(expected, newUserBean.getEmail());
        }
    }

    private void assertFieldsClear() {
        assertNull(newUserBean.getUsername());
        assertNull(newUserBean.getPassword());
        assertNull(newUserBean.getRepeated());
        assertNull(newUserBean.getFirstName());
        assertNull(newUserBean.getLastName());
        assertNull(newUserBean.getEmail());
        assertNull(newUserBean.getUserRole());
        assertTrue(newUserBean.isEnabled());
    }
}
