package at.timeguess.backend.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Enumeration of available user roles.
 */
public enum UserRole {

    ADMIN,
    MANAGER,
    PLAYER;

    private static EnumSet<UserRole> userRoles = EnumSet.allOf(UserRole.class);

    /**
     * Returns a set containing all available user roles.
     * @return set of all user roles
     */
    public static EnumSet<UserRole> getUserRoles() {
        return userRoles;
    }

    /**
     * Maps the given set of roles to a specific user role according to apps inheritance rules.
     * @param roles roles
     * @return resulting role
     */
    public static UserRole mapUserRole(Set<UserRole> roles) {
        if (roles != null) {
            if (roles.contains(ADMIN)) return ADMIN;
            else if (roles.contains(MANAGER)) return MANAGER;
        }
        return PLAYER;
    }

    /**
     * Maps the given user role to a set of roles according to apps inheritance rules.
     * @param role role
     * @return set of roles
     */
    public static Set<UserRole> mapUserRole(UserRole role) {
        Set<UserRole> roles = new HashSet<>();
        switch (role) {
            case ADMIN:
                roles.add(UserRole.ADMIN);
            case MANAGER:
                roles.add(UserRole.MANAGER);
                break;
            default:
                roles.add(UserRole.PLAYER);
        }
        return roles;
    }
}
