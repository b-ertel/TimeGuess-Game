package at.timeguess.backend.ui.websockets;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import javax.websocket.CloseReason.CloseCode;

import org.omnifaces.cdi.push.SocketEvent;
import org.omnifaces.cdi.push.SocketEvent.Closed;
import org.omnifaces.cdi.push.SocketEvent.Opened;
import org.omnifaces.cdi.push.SocketEvent.Switched;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import at.timeguess.backend.events.ChannelPresenceEventPublisher;
import at.timeguess.backend.spring.ApplicationContextProvider;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.Utils;

@Named
@ApplicationScoped
public class ChannelObserver implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelObserver.class);

    @Autowired
    private ChannelPresenceEventPublisher channelPresenceEventPublisher;

    // ids of users present in channels
    private Map<String, Set<Long>> channelPresence = new ConcurrentHashMap<String, Set<Long>>();

    /**
     * @apiNote autowiring doesn't work with the {@link ApplicationScoped} annotation
     *          (neither does {@link CDIAutowired}, independent of the package the class is located in)
     *          but without the annotation the necessary events are not received, so autowiring is done manually
     */
    @PostConstruct
    public void setup() {
        if (channelPresenceEventPublisher == null)
            ApplicationContextProvider.autowire(this, channelPresenceEventPublisher);
    }

    /**
     * Removes user leaving channel from stored list, if present. Also sends current userId list to registered
     * subscribers.
     * @param event
     */
    public void onClose(@Observes @Closed SocketEvent event) {
        String channel = event.getChannel(); // returns <o:socket channel>
        Long userId = event.getUser(); // returns <o:socket user>, if any
        CloseCode code = event.getCloseCode(); // returns close reason code

        setAbsent(userId, channel);
        publishChange(channel);

        LOGGER.info("user {} closed channel {} (code {})", userId == null ? 0 : userId, channel, code.getCode());
    }

    /**
     * Adds user entering channel to stored list, if present. Also sends current userId list to registered subscribers.
     * @param event
     * @apiNote a single person can open multiple sockets on same channel/user
     */
    public void onOpen(@Observes @Opened SocketEvent event) {
        String channel = event.getChannel(); // returns <o:socket channel>
        Long userId = event.getUser(); // returns <o:socket user>, if any

        setPresent(userId, channel);
        publishChange(channel);

        LOGGER.info("user {} opened channel {}", userId == null ? 0 : userId, channel);
    }

    /**
     * Changes switching users in stored list. Also sends current userId list to registered subscribers.
     * @param event
     */
    public void onSwitch(@Observes @Switched SocketEvent event) {
        String channel = event.getChannel(); // returns <o:socket channel>
        Long currentUserId = event.getUser(); // returns current <o:socket user>, if any
        Long previousUserId = event.getPreviousUser(); // returns previous <o:socket user>, if any

        setAbsent(previousUserId, channel);
        setPresent(currentUserId, channel);
        publishChange(channel);

        LOGGER.info("user {} switched to {} on channel {}", previousUserId == null ? 0 : previousUserId,
                currentUserId == null ? 0 : currentUserId, channel);
    }

    private void publishChange(String channel) {
        channelPresenceEventPublisher.publishChannelPresenceEvent(channel,
                Collections.unmodifiableSet(channelPresence.getOrDefault(channel, Set.of())));
    }

    private void setAbsent(Long userId, String channel) {
        Utils.removeFromSet(channelPresence, channel, userId);
    }

    private void setPresent(Long userId, String channel) {
        Utils.addToSet(channelPresence, channel, userId);
    }
}