package at.timeguess.backend.ui.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import at.timeguess.backend.ui.websockets.WebSocketManager;
import at.timeguess.backend.utils.CDIAutowired;
import at.timeguess.backend.utils.CDIContextRelated;

/**
 * A controller for assigning values of the facets characteristic of a TimeFlip device
 * to {@link CubeFace} objects.
 */
@Controller
@Scope("session")
@CDIContextRelated
public class CubeConfigurationController implements Consumer<UnconfiguredFacetsEvent> {

    private static final String MOVE_INSTRUCTION = "Move the cube such that the facet to be assigned is at the top!";
    private static final String CLICK_INSTRUCTION = "Click one of the buttons below to assign the recieved facet number to a cube face!";

    @Autowired
    private CubeService cubeService;
    @Autowired
    private MessageBean messageBean;
    @Autowired
    UnconfiguredFacetsEventListener unconfiguredfacetsEventListener;
    @CDIAutowired
    private WebSocketManager websocketManager;

    private Cube cube;
    private Integer facet; 
    private String instruction;
    private List<CubeFace> cubeFaces; 
    private Map<CubeFace, Integer> mapping;
    private Map<CubeFace, Integer> previousMapping;

    @PostConstruct
    public void init() {
        unconfiguredfacetsEventListener.subscribe(this);
    }

    @PreDestroy
    public void destroy() {
        unconfiguredfacetsEventListener.unsubscribe(this);
    }

    /**
     * Prepare the controller for a fresh attempt to configure a cube.
     * 
     * @param cubeToConfigure the cube to configure
     */
    public void prepareForNewConfiguration(Cube cubeToConfigure) {
        cube = cubeToConfigure;
        facet = null;
        instruction = MOVE_INSTRUCTION;
        cubeFaces = cubeService.allCubeFaces();
        mapping = new ConcurrentHashMap<>();
        previousMapping = new ConcurrentHashMap<>();
        for (CubeFace cubeFace : cubeFaces) {
            Integer mappedFacet = cubeService.getMappedFacet(cube, cubeFace);
            if (mappedFacet != null) {
                mapping.put(cubeFace, mappedFacet);
                previousMapping.put(cubeFace, mappedFacet);
            }
        }
        cubeService.deleteConfigurations(cube);
    }

    @Override
    public synchronized void accept(UnconfiguredFacetsEvent unconfiguredFacetsEvent) {
        if (cube.equals(unconfiguredFacetsEvent.getCube())) {
            facet = unconfiguredFacetsEvent.getFacet();
            instruction = CLICK_INSTRUCTION;
            websocketManager.getCubeConfigurationChannel().send("newCubeFace");
        }
    }

    /**
     * Check if a facet number has already been assigned to a given cube face.
     *  
     * @param cubeFace the cube face
     * @return a boolean indicating if a facet number has already been assigned
     */
    public boolean hasAssignedFacetNumber(CubeFace cubeFace) {
        return mapping.containsKey(cubeFace);
    }

    /**
     * Get the facet number assigned to a given cube face.
     * 
     * @param cubeFace the cube face
     * @return the assigned facet number if one exists or null otherwise
     */
    public Integer getAssignedFacetNumber(CubeFace cubeFace) {
        return mapping.get(cubeFace);
    }

    /**
     * Assign the cached facet number to a given cube face.
     * 
     * Warns the user if no facet number is available for assigning or
     * if the cached facet number has already been assigned to a cube face. 
     * 
     * @param cubeFace the cube face
     */
    public void assignFacetNumber(CubeFace cubeFace) {
        if (facet == null) {
            messageBean.alertWarning("CubeConfiguration", "No Facet number available to assign!");
        }
        else {
            if (mapping.containsValue(facet)) {
                messageBean.alertWarning("CubeConfiguration", "Facet number has already been assigned!");
            }
            else {
                mapping.put(cubeFace, facet);
                facet = null;
                instruction = MOVE_INSTRUCTION;
                messageBean.alertInformation("CubeConfiguration", "Facet number successfully assigned!");
            }
        }
    }

    /**
     * Remove the assigned facet from a given cube face. 
     * @param cubeFace cubeface
     */
    public void removeAssignedFacetNumber(CubeFace cubeFace) {
        mapping.remove(cubeFace);
        messageBean.alertInformation("CubeConfiguration", "Reset successful!");
    }

    public boolean isConfigured() {
        return mapping.size() == cubeFaces.size();
    }

    public void submit() {
        if (isConfigured()) {
            try {
                cubeService.saveMappingForCube(cube, mapping);
                messageBean.alertInformation("CubeConfiguration", "Configuration successfully saved!");
            }
            catch (IllegalArgumentException e) {
                messageBean.alertErrorFailValidation("Cube Configuration", e.getMessage());
            }
        }
    }

    public void abort() {
        try {
            cubeService.saveMappingForCube(cube, previousMapping);
        }
        catch (IllegalArgumentException e) {
            messageBean.alertErrorFailValidation("Cube Configuration", e.getMessage());
        }
    }

    public Cube getCube() {
        return cube;
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
