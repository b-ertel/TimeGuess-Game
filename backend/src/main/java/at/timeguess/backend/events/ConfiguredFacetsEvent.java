package at.timeguess.backend.events;

import org.springframework.context.ApplicationEvent;

import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

/**
 * A custom application event indicating that a TimeFlip device which is known
 * to the system and has a valid configuration has reported a new
 * value of its facets characteristic.
 * 
 * It contains information about the cube and the cube face associated with
 * the reported facet number.
 */
public class ConfiguredFacetsEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final Cube cube; // the cube that reported the change
    private final CubeFace cubeFace; // the cube face associated with the reported facet number

    public ConfiguredFacetsEvent(Object source, Cube cube, CubeFace cubeFace) {
        super(source);
        this.cube = cube;
        this.cubeFace = cubeFace;
    }

    public Cube getCube() {
        return cube;
    }

    public CubeFace getCubeFace() {
        return cubeFace;
    }

}
