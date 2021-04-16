package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.timeguess.backend.ui.controllers.demo.CubeStatusController;
import at.timeguess.backend.ui.controllers.demo.WebSocketCubeFace;

/**
 * A custom event listener for cube status changes.
 */
@Component
public class OnboardingEventListener implements ApplicationListener<OnboardingEvent> {

    @Autowired
    private CubeStatusController cubeController;

    @Override
    public void onApplicationEvent(OnboardingEvent onboardingEvent) {
        cubeController.statusChange();
    }

}
