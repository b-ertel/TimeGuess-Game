package at.timeguess.backend.ui.beans;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;

import at.timeguess.backend.model.UserRole;

/**
 * Tests for {@link SessionInfoBean}.
 */
@SpringBootTest
@WebAppConfiguration
public class SessionInfoBeanTest {

    @Autowired
    SessionInfoBean sessionInfoBean;

    @Test
    @WithMockUser(username = "user1", authorities = { "PLAYER" })
    public void testLoggedIn() {
        assertTrue(sessionInfoBean.isLoggedIn(), "sessionInfoBean.isLoggedIn does not return true for authenticated user");
        assertEquals("user1", sessionInfoBean.getCurrentUserName(), "sessionInfoBean.getCurrentUserName does not return authenticated user's name");
        assertEquals("user1", sessionInfoBean.getCurrentUser().getUsername(), "sessionInfoBean.getCurrentUser does not return authenticated user");
        assertEquals("PLAYER", sessionInfoBean.getCurrentUserRoles(), "sessionInfoBean.getCurrentUserRoles does not return authenticated user's roles");
        assertTrue(sessionInfoBean.hasRole("PLAYER"), "sessionInfoBean.hasRole does not return true for a role the authenticated user has");
        assertFalse(sessionInfoBean.hasRole("ADMIN"), "sessionInfoBean.hasRole does not return false for a role the authenticated user does not have");
    }

    @Test
    public void testNotLoggedIn() {
        assertFalse(sessionInfoBean.isLoggedIn(), "sessionInfoBean.isLoggedIn does return true for not authenticated user");
        assertEquals("", sessionInfoBean.getCurrentUserName(), "sessionInfoBean.getCurrentUserName does not return empty string when not logged in");
        assertNull(sessionInfoBean.getCurrentUser(), "sessionInfoBean.getCurrentUser does not return null when not logged in");
        assertEquals("", sessionInfoBean.getCurrentUserRoles(), "sessionInfoBean.getCurrentUserRoles does not return empty string when not logged in");
        for (UserRole role : UserRole.values()) {
            assertFalse(sessionInfoBean.hasRole(role.name()), "sessionInfoBean.hasRole does not return false for all possible roales");
        }
    }

    @Test
    @WithAnonymousUser
    public void testNotLoggedInAnonymous() {
        assertFalse(sessionInfoBean.isLoggedIn());
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    public void testGetCurrentUser() {
        assertEquals("admin", sessionInfoBean.getCurrentUser().getUsername());
    }

    @Test
    public void testHasRole() {
        assertFalse(sessionInfoBean.hasRole(null));
        assertFalse(sessionInfoBean.hasRole(""));
        assertFalse(sessionInfoBean.hasRole("admin"));
    }
}
