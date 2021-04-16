package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * A custom event publisher for cube status changes.
 */
@Component
public class OnboardingEventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishOnboardingEvent() {
        OnboardingEvent onboardingEvent = new OnboardingEvent(this);
        applicationEventPublisher.publishEvent(onboardingEvent);
    }

}
