package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * A custom event publisher for facets changes.
 */
@Component
public class FacetsEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishFacetsEvent() {
        FacetsEvent facetsEvent = new FacetsEvent(this);
        applicationEventPublisher.publishEvent(facetsEvent);
    }

}
