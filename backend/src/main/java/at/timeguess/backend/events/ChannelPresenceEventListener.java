package at.timeguess.backend.events;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.timeguess.backend.utils.Utils;

/**
 * A component that listens to custom application events of type {@link ChannelPresenceEvent} and provides methods for
 * other Spring managed components to subscribe to or unsubscribe from receiving such events.
 * This is particularly useful for components that have a scope that is more narrow since they cannot listen to those events themselves.
 */
@Component
public class ChannelPresenceEventListener implements ApplicationListener<ChannelPresenceEvent> {

    private Map<String, Set<Consumer<ChannelPresenceEvent>>> subscribers = new ConcurrentHashMap<>();

    /**
     * Subscribe to events for given channel.
     * @param channel    the channel for which to subscribe
     * @param subscriber the subscriber
     */
    public void subscribe(String channel, Consumer<ChannelPresenceEvent> subscriber) {
        Utils.addToSet(subscribers, channel, subscriber);
    }

    /**
     * Unsubscribe from events for given channel.
     * @param channel    the channel for which to unsubscribe
     * @param subscriber the subscriber
     */
    public void unsubscribe(String channel, Consumer<ChannelPresenceEvent> subscriber) {
        Utils.removeFromSet(subscribers, channel, subscriber);
    }

    @Override
    public void onApplicationEvent(ChannelPresenceEvent channelPresenceEvent) {
        Set<Consumer<ChannelPresenceEvent>> subs = subscribers.get(channelPresenceEvent.getChannel());
        if (subs != null) for (Consumer<ChannelPresenceEvent> subscriber : subs) {
            subscriber.accept(channelPresenceEvent);
        }
    }
}
