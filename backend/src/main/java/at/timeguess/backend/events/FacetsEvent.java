package at.timeguess.backend.events;

import org.springframework.context.ApplicationEvent;

/**
 * A custom event for facets changes.
 */
public class FacetsEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public FacetsEvent(Object source) {
        super(source);
    }

}
