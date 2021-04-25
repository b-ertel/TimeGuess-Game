package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

/**
 * A component that provides a method to publish a custom application event
 * of type {@link ConfiguredFacetsEvent}.
 */
@Component
public class ConfiguredFacetsEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publish a new {@link ConfiguredFacetsEvent}.
     * 
     * @param cube the cube
     * @param cubeFace the cube face
     */
    public void publishConfiguredFacetsEvent(Cube cube, CubeFace cubeFace) {
        ConfiguredFacetsEvent configuredFacetsEvent = new ConfiguredFacetsEvent(this, cube, cubeFace);
        applicationEventPublisher.publishEvent(configuredFacetsEvent);
    }

}
