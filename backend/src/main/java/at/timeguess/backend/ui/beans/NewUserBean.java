package at.timeguess.backend.ui.beans;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.UserRole;
import at.timeguess.backend.services.UserService;

/**
 * Bean for creating a new user.
 */
@Component
@Scope("view")
public class NewUserBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageBean messageBean;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String username;
    private String password;
    private String repeated;

    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private UserRole userRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeated() {
        return repeated;
    }

    public void setRepeated(String repeated) {
        this.repeated = repeated;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Clears all fields.
     */
    public void clearFields() {
        this.setUsername(null);
        this.setPassword(null);
        this.setFirstName(null);
        this.setLastName(null);
        this.setEmail(null);
        this.setUserRole(null);
        this.setEnabled(false);
    }

    /**
     * Creates a new user with the role of player.
     * @apiNote shows a ui message if input field are invalid.
     */
    public void createUser() {
        this.createUserFromFields(false, user -> user.getRoles().add(UserRole.PLAYER));
    }

    /**
     * Creates a new user with the set role.
     * @apiNote shows a ui message if input field are invalid.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void createUserAdmin() {
        this.createUserFromFields(true, user -> {
            Set<UserRole> roles = user.getRoles();
            switch (this.getUserRole()) {
            case ADMIN:
                roles.add(UserRole.ADMIN);
            case MANAGER:
                roles.add(UserRole.MANAGER);
                break;
            default:
                roles.add(UserRole.PLAYER);
            }
        });
    }

    /**
     * Checks if all fields contain valid values.
     * @param isAdmin true when registering via administrative interface,
     *                false when registering via user registration interface
     * @return true if all necessary fields contain valid values, false otherwise
     */
    public boolean validateInput(boolean isAdminAccess) {
        if (Strings.isBlank(getUsername()) || userService.hasUser(getUsername())) return false;
        if (Strings.isBlank(getPassword())) return false;

        if (isAdminAccess) {
            if (Strings.isBlank(getFirstName())) return false;
            if (Strings.isBlank(getLastName())) return false;
            if (Strings.isBlank(getEmail())) return false;
        }
        else {
            if (Strings.isBlank(getRepeated())) return false;
            if (!getPassword().equals(getRepeated())) return false;
        }

        return true;
    }

    /**
     * Creates a new user instance, sets its fields from set values (excepts roles) and saves it to the database.
     */
    private void createUserFromFields(boolean isAdmin, Consumer<User> consumer) {
        if (this.validateInput(isAdmin)) {
            User user = new User();
            user.setUsername(getUsername());
            user.setFirstName(getFirstName());
            user.setLastName(getLastName());
            user.setEmail(getEmail());
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode(getPassword()));
            consumer.accept(user);

            this.userService.saveUser(user);
        }
        else {
            messageBean.alertErrorFailValidation("User creation failed", "Input fields are invalid");
        }
    }
}
