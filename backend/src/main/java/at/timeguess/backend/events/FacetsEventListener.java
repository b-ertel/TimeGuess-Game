package at.timeguess.backend.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import at.timeguess.backend.ui.controllers.demo.WebSocketCubeFace;

/**
 * A custom event listener for facets changes.
 */
@Component
public class FacetsEventListener implements ApplicationListener<FacetsEvent> {

    @Autowired
    private WebSocketCubeFace webSocketCubeFace;

    @Override
    public void onApplicationEvent(FacetsEvent facetsEvent) {
        webSocketCubeFace.cubeChange();
    }

}
