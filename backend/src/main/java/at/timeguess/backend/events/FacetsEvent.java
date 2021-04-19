package at.timeguess.backend.events;

import org.springframework.context.ApplicationEvent;

import at.timeguess.backend.model.Cube;

/**
 * An application event indicating that a known TimeFlip device
 * has reported to the backend a new value of its facets characteristic.
 * 
 * We use application events to get information like this from
 * the application tier to the presentation tier without violating
 * the three-tier-architecture.
 */
public class FacetsEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Cube cube; // the cube that reported the change
    private final Integer facet; // the new value of the facets characteristic

    public FacetsEvent(Object source, Cube cube, Integer facet) {
        super(source);
        this.cube = cube;
        this.facet = facet;
    }

    public Cube getCube() {
        return cube;
    }

    public Integer getFacet() {
        return facet;
    }

}
