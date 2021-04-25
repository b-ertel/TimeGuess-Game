package at.timeguess.backend.events;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * A component that listens to custom application events of type
 * {@link UnconfiguredFacetsEvent} and provides methods for other Spring managed
 * components to subscribe to or unsubscribe from recieving such events.
 * 
 * This is particularly useful for components that have a scope that is more narrow
 * since they cannot listen to those events themselves.
 */
@Component
public class UnconfiguredFacetsEventListener implements ApplicationListener<UnconfiguredFacetsEvent> {

    private List<Consumer<UnconfiguredFacetsEvent>> subscribers = new CopyOnWriteArrayList<>();
        
    /**
     * Subscribe for events.
     * 
     * @param subscriber the subscriber
     */
    public void subscribe(Consumer<UnconfiguredFacetsEvent> subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Unsubscribe from events.
     * 
     * @param subscriber the subscriber
     */
    public void unsubscribe(Consumer<UnconfiguredFacetsEvent> subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void onApplicationEvent(UnconfiguredFacetsEvent unconfiguredFacetsEvent) {
        for (Consumer<UnconfiguredFacetsEvent> subscriber : subscribers) {
            subscriber.accept(unconfiguredFacetsEvent);
        }
    }

}
