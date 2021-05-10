package at.timeguess.backend.events;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * A component that provides a method to publish a custom application event of type {@link ChannelPresenceEvent}.
 */
@Component
public class ChannelPresenceEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publish a new {@link ChannelPresenceEvent}.
     * @param channel channel the presence changed in
     * @param userIds the user ids currently present in the channel
     */
    public void publishChannelPresenceEvent(String channel, Set<Long> userIds) {
        ChannelPresenceEvent channelPresenceEvent = new ChannelPresenceEvent(this, channel, userIds);
        applicationEventPublisher.publishEvent(channelPresenceEvent);
    }
}
