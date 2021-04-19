package at.timeguess.backend.events;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.timeguess.backend.ui.controllers.demo.WebSocketCubeFace;

/**
 * A component that listens for events of type {@link FacetsEvent}.
 * 
 * Although not explicitly stated, we consider this component a part of the presentation tier,
 * since it passes the information from the event to controllers.
 * 
 * Note that it has the same scope (in this case the default "singleton") as the respective
 * listener, as otherwise the events would not get delivered correctly.
 * 
 * This class also offers the possibility to subscribe and unsubscribe for events which is
 * needed for controllers with a scope different from that of the publisher and the listener
 * in order to recieve them, since they cannot be listeners themselves.
 */
@Component
public class FacetsEventListener implements ApplicationListener<FacetsEvent> {

    private Set<Consumer<FacetsEvent>> consumers = new CopyOnWriteArraySet<>();

    @Autowired
    private WebSocketCubeFace webSocketCubeFace;

    @Override
    public void onApplicationEvent(FacetsEvent facetsEvent) {
        webSocketCubeFace.cubeChange();
        // TODO: send only to consumers that are interested in the cube from the event
        for (Consumer<FacetsEvent> consumer : consumers) {
            consumer.accept(facetsEvent);
        }
    }

    /**
     * Subscribe to the listener to recieve FacetsEvents.
     * 
     * @param consumer a consumer of {@link FacetsEvent} (e.g., controller)
     */
    public void subscribe(Consumer<FacetsEvent> consumer) {
        consumers.add(consumer);
    }

    /**
     * Unsubscribe from the listener to recieve FacetsEvents.
     * 
     * @param consumer a consumer of {@link FacetsEvent} (e.g., controller)
     */
    public void unsubscribe(Consumer<FacetsEvent> consumer) {
        consumers.remove(consumer);
    }

}
