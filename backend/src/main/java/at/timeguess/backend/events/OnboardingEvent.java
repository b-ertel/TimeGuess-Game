package at.timeguess.backend.events;

import org.springframework.context.ApplicationEvent;

/**
 * A custom event for cube status
 */
public class OnboardingEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    public OnboardingEvent(Object source) {
        super(source);
    }

}
