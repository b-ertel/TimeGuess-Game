package at.timeguess.backend.ui.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import at.timeguess.backend.events.UnconfiguredFacetsEvent;
import at.timeguess.backend.events.UnconfiguredFacetsEventListener;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.services.CubeService;
import at.timeguess.backend.ui.beans.MessageBean;
import at.timeguess.backend.ui.beans.SessionInfoBean;
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * A controller for assigning {@link CubeFace} objects to values of the
 * facets characteristic of a TimeFlip device.
 */
@Controller
@Scope("session")
@CDIContextRelated
public class CubeConfigurationController implements Consumer<UnconfiguredFacetsEvent> {

    private static final String MOVE_INSTRUCTION = "Move the cube such that the facet to be assigned is at the top!";
    private static final String CLICK_INSTRUCTION = "Click one of the buttons below to assign!";

    @Autowired
    private CubeService cubeService;
    @Autowired
    private SessionInfoBean sessionInfoBean;
    @Autowired
    private MessageBean messageBean;
    @Autowired
    UnconfiguredFacetsEventListener unconfiguredfacetsEventListener;
    @CDIAutowired
    private WebSocketManager websocketManager;

    private Cube cube;
    private Integer facet = null;
    private String instruction = MOVE_INSTRUCTION;
    private List<CubeFace> cubeFaces;
    private Map<CubeFace, Integer> mapping = new HashMap<>();

    @PostConstruct
    public void init() {
        cubeFaces = cubeService.allCubeFaces();
        unconfiguredfacetsEventListener.subscribe(this);
    }

    @PreDestroy
    public void destroy() {
        unconfiguredfacetsEventListener.unsubscribe(this);
    }

    /**
     * A method for processing a {@link UnconfiguredFacetsEvent}.
     */
    @Override
    public synchronized void accept(UnconfiguredFacetsEvent unconfiguredFacetsEvent) {
        if (cube.equals(unconfiguredFacetsEvent.getCube())) {
            facet = unconfiguredFacetsEvent.getFacet();
            instruction = CLICK_INSTRUCTION;
            websocketManager.getCubeConfigurationChannel().send("newCubeFace");
        }
    }

    public Integer getAssignedFacetNumber(CubeFace cubeFace) {
        return mapping.get(cubeFace);
    }

    public void assign(CubeFace cubeFace) {
        if (facet != null) {
            for (CubeFace assignedCubeFace : mapping.keySet()) {
                if (mapping.get(assignedCubeFace).equals(facet)) {
                    mapping.remove(assignedCubeFace);
                }
            }
            mapping.put(cubeFace, facet);
            facet = null;
            instruction = MOVE_INSTRUCTION;
            messageBean.alertInformation("CubeConfiguration", "Facet successfully assigned!");
        }
    }

    public boolean isConfigured() {
        return mapping.size() == cubeFaces.size();
    }

    public void submit() {
        if (isConfigured()) {
            cubeService.saveMappingForCube(cube, mapping);
            messageBean.alertInformation("CubeConfiguration", "Configuration successfully saved!");
        }
    }

    public Cube getCube() {
        return cube;
    }

    public void setCube(Cube cube) {
        this.cube = cube;
        this.facet = null;
        this.instruction = MOVE_INSTRUCTION;
    }

    public Integer getFacet() {
        return facet;
    }

    public String getInstruction() {
        return instruction;
    }

    public List<CubeFace> getCubeFaces() {
        return Collections.unmodifiableList(cubeFaces);
    }

}

