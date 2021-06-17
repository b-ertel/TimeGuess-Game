package at.timeguess.backend.events;

import java.util.Set;

import org.springframework.context.ApplicationEvent;

/**
 * A custom application event indicating that the user presence in a channel changed.
 * It contains information about the channel and the user ids currently present in the channel.
 */
public class ChannelPresenceEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final String channel;
    private final Set<Long> userIds;

    public ChannelPresenceEvent(Object source, String channel, Set<Long> userIds) {
        super(source);
        this.channel = channel;
        this.userIds = userIds;
    }

    /**
     * the channel the presence changed in
     * @return channel name
     */
    public String getChannel() {
        return channel;
    }

    /**
     * the user ids currently present in the channel
     * @return list of user ids
     */
    public Set<Long> getUserIds() {
        return userIds;
    }
}
