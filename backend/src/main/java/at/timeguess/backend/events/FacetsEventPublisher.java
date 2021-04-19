package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Cube;

/**
 * A component that provides a method to publish a {@link FacetsEvent}.
 * 
 * Although not explicitly stated, we consider this component a part of the application tier,
 * since it is used by services to bring information to controllers.
 * 
 * Note that it has the same scope (in this case the default "singleton") as the respective
 * listener, as otherwise the events would not get delivered correctly.
 */
@Component
public class FacetsEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishFacetsEvent(Cube cube, Integer facet) {
        FacetsEvent facetsEvent = new FacetsEvent(this, cube, facet);
        applicationEventPublisher.publishEvent(facetsEvent);
    }

}
