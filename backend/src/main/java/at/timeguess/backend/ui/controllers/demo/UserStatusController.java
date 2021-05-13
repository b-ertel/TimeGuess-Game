package at.timeguess.backend.ui.controllers.demo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.model.User;
import at.timeguess.backend.model.LogEntry;
import at.timeguess.backend.model.LogEntryType;
import at.timeguess.backend.model.UserStatus;
import at.timeguess.backend.model.UserStatusInfo;
import at.timeguess.backend.repositories.UserRepository;
import at.timeguess.backend.spring.UserStatusInitializationHandler;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * This controller holds and manages all user's status-information (i.e. their online-status).
 */
@Controller
@Scope("application")
@CDIContextRelated
public class UserStatusController {

    @Autowired
    private UserRepository userRepository;
    @CDIAutowired
    private WebSocketManager websocketManager;
    private Map<String, UserStatusInfo> userStatus = new ConcurrentHashMap<>();
    private List<LogEntry> actionLogs = new CopyOnWriteArrayList<>();

    /**
     * Called by the {@link UserStatusInitializationHandler}: When the database-connection is established,
     * all users can be retrieved and the collection holding the user-status can be setup.
     * NOTE (original): The {@link UserStatusInitializationHandler} calls this setup-method only once at startup.
     * If you add any other users, they will not appear within the user-status-collection.
     * You either need to restart the application (feasible in development-mode, really bad behavior in production)
     * or call this setup.method again to refresh the mentioned collection
     * 
     * NOTE (changes): the code was adapted to deal with newly registered users:
     * - for every status change call the username is searched for in the saved map, which is reloaded from database if the name is not found
     * - when the username is not found in the database, a dummy user with status {@linkplain LogEntryType.USER_UNKNOWN} is inserted in the actionlog
     * - the map setup from database does not overwrite existing entries anymore
     */
    public void setupUserStatus() {
        this.userRepository.findAll().forEach(user -> {
            String username = user.getUsername();
            if (!this.userStatus.containsKey(username))
                this.userStatus.put(username, new UserStatusInfo(user));
        });
    }

    /**
     * Convenience-method.
     * See {@link #afterStatusChange(String, UserStatus, LogEntryType)}
     * @param username
     */
    public void afterLogin(String username) {
        this.afterStatusChange(username, UserStatus.ONLINE, LogEntryType.USER_LOGIN);
    }

    /**
     * Convenience-method. See {@link #afterStatusChange(String, UserStatus, LogEntryType)}
     * @param username
     */
    public void afterLogout(String username) {
        this.afterStatusChange(username, UserStatus.OFFLINE, LogEntryType.USER_LOGOUT);
    }

    /**
     * After a users logs -in or -out, its status is changed and a corresponding event-log is appended.
     * @param username  The user which performs the action
     * @param newStatus The new user-status
     * @param logType   The log-type for the event to collect
     */
    private void afterStatusChange(String username, UserStatus newStatus, LogEntryType logType) {
        UserStatusInfo status = this.getUserStatus(username);
        if (status == null) {
            User unk = new User();
            unk.setUsername(username);
            this.log(unk, LogEntryType.USER_UNKNOWN);
        }
        else {
            User user = this.userRepository.findFirstByUsername(username);
            // change status
            status.setStatus(newStatus);
            // append log
            this.log(user, logType);
            // notify all users (application-wide)
            this.websocketManager.getUserRegistrationChannel().send("connectionUpdate");
        }
    }

    /**
     * Simply appends a log-entry to the action-log.
     * @param user
     * @param type
     */
    private void log(User user, LogEntryType type) {
        this.actionLogs.add(new LogEntry(user, type));
    }

    public Collection<UserStatusInfo> getUserStatusInfos() {
        return Collections.unmodifiableCollection(this.userStatus.values());
    }

    public List<LogEntry> getActionLogs() {
        return Collections.unmodifiableList(this.actionLogs);
    }

    /**
     * Returns {@link UserStatusInfo} of given user, or null if the user is not in the database.
     * @param username username of user to retrieve status for
     * @return {@link UserStatusInfo} for given user
     * @apiNote if the given user is not already saved the map is setup from db again
     */
    private UserStatusInfo getUserStatus(String username) {
        UserStatusInfo ret = this.userStatus.get(username);
        if (ret == null) {
            // reload from db
            this.setupUserStatus();
            ret = this.userStatus.get(username);
        }
        return ret;
    }
}
