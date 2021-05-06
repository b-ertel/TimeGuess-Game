package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;

/**
 * A component that provides a method to publish a custom application event
 * of type {@link UnconfiguredFacetsEvent}.
 */
@Component
public class UnconfiguredFacetsEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publish a new {@link UnconfiguredFacetsEvent}.
     * 
     * @param cube the cube
     * @param facet the facet number
     */
    public void publishUnconfiguredFacetsEvent(Cube cube, Integer facet) {
        UnconfiguredFacetsEvent unconfiguredFacetsEvent = new UnconfiguredFacetsEvent(this, cube, facet);
        applicationEventPublisher.publishEvent(unconfiguredFacetsEvent);
    }

}
