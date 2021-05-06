package at.timeguess.backend.ui.controllers;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.events.ConfiguredFacetsEvent;
import at.timeguess.backend.events.ConfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * A controller for testing the assignment of {@link CubeFace} objects to values of the
 * facets characteristic of a TimeFlip device.
 */
@Controller
@Scope("session")
@CDIContextRelated
public class CubeTestController implements Consumer<ConfiguredFacetsEvent> {

    @Autowired
    private ConfiguredFacetsEventListener configuredfacetsEventListener;
    @CDIAutowired
    private WebSocketManager websocketManager;

    private Cube cube;
    private CubeFace cubeFace;

    @PostConstruct
    public void init() {
        configuredfacetsEventListener.subscribe(this);
    }

    @PreDestroy
    public void destroy() {
        configuredfacetsEventListener.unsubscribe(this);
    }
    
    /**
     * Prepare the controller for a fresh attempt to test a cube.
     * 
     * @param cube the cube to configure
     */
    public void prepareForNewTest(Cube cubeToConfigure) {
        cube = cubeToConfigure;
        cubeFace = null;
    }

    /**
     * A method for processing a {@link ConfiguredFacetsEvent}.
     */
    @Override
    public synchronized void accept(ConfiguredFacetsEvent configuredFacetsEvent) {
        if (cube.equals(configuredFacetsEvent.getCube())) {
            cubeFace = configuredFacetsEvent.getCubeFace();
            websocketManager.getCubeTestChannel().send("newCubeFace");
        }
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
    }

    public CubeFace getCubeFace() {
        return cubeFace;
    }

    public void setCubeFace(CubeFace cubeFace) {
        this.cubeFace = cubeFace;
    }

}
