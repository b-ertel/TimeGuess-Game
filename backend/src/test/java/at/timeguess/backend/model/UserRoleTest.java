package at.timeguess.backend.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UserRole}.
 */
public class UserRoleTest {

    @Test
    public void testGetUserRoles() {
        Collection<UserRole> roles = UserRole.getUserRoles();

        assertTrue(roles.contains(UserRole.PLAYER));
        assertTrue(roles.contains(UserRole.MANAGER));
        assertTrue(roles.contains(UserRole.ADMIN));
    }

    @Test
    public void testMapUserRoleFromSet() {
        Set<UserRole> roles = EnumSet.copyOf(UserRole.getUserRoles());
        assertEquals(UserRole.ADMIN, UserRole.mapUserRole(roles));

        roles.remove(UserRole.ADMIN);
        assertEquals(UserRole.MANAGER, UserRole.mapUserRole(roles));

        roles.remove(UserRole.MANAGER);
        assertEquals(UserRole.PLAYER, UserRole.mapUserRole(roles));

        roles = null;
        assertEquals(UserRole.PLAYER, UserRole.mapUserRole(roles));
    }

    @Test
    public void testMapUserRoleToSet() {
        assertEquals(Set.of(UserRole.PLAYER), UserRole.mapUserRole(UserRole.PLAYER));
        assertEquals(Set.of(UserRole.MANAGER), UserRole.mapUserRole(UserRole.MANAGER));
        assertEquals(Set.of(UserRole.ADMIN, UserRole.MANAGER), UserRole.mapUserRole(UserRole.ADMIN));
    }
}
