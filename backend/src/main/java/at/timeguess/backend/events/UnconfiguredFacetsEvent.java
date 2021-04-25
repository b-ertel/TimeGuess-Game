package at.timeguess.backend.events;

import org.springframework.context.ApplicationEvent;

import at.timeguess.backend.model.Cube;

/**
 * A custom application event indicating that a TimeFlip device which is known
 * to the system but does not have a valid configuration has reported a new
 * value of its facets characteristic.
 * 
 * It contains information about the cube and the facet number.
 */
public class UnconfiguredFacetsEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Cube cube; // the cube that reported the change
    private final Integer facet; // the new value of its facets characteristic

    public UnconfiguredFacetsEvent(Object source, Cube cube, Integer facet) {
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
