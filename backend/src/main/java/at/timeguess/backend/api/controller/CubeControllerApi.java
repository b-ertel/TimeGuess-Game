package at.timeguess.backend.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.api.services.CubeService;
import at.timeguess.backend.model.Cube;

import at.timeguess.backend.model.api.BatteryLevelMessage;
import at.timeguess.backend.model.api.BatteryLevelResponse;
import at.timeguess.backend.model.api.ConnectedMessage;
import at.timeguess.backend.model.api.ConnectedResponse;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;
import at.timeguess.backend.model.api.RSSIMessage;
import at.timeguess.backend.model.api.RSSIResponse;
import at.timeguess.backend.ui.controllers.CubeController;

/**
 *  api controller which handles httpRequests of new {@link Cube} entities 
 *
 */

@RestController
public class CubeControllerApi {
	
	@Autowired
	private CubeController cubeController;
	
	/**
	 * process the physical TimeFlip device which is represented by Cube entity in our model
	 * 
	 * @param cube the Cube
	 * @return Cube entity (at the moment)
	 */
	@PostMapping("/api/cube")
	public Cube createCube(@RequestBody Cube cube) {
		
		// do something .. 
		
		
	/*	
	 * this logic may be implemented in UI
	 * 
	 * if(cube.isConfigured()) {
			// cube is known
		}
		else if(!cube.isConfigured() && isMacAddressKnown(cube)) {
			
			// cube is known and has no configuration (i.e. first time to configure or configuration is lost)
		}
		
		else {
			
			// cube is either configured nor is it known - admin has to register the cube first before configuration is possible
			
		}
		*/
		return cubeController.registerCube(cube);
	}
	
    /**
     * Process messages from a TimeFlip device signaling
     * a change of the Battery level characteristic.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/batterylevel")
    private BatteryLevelResponse processBatteryLevel(@RequestBody BatteryLevelMessage message) {
        // do something ...
        BatteryLevelResponse response = new BatteryLevelResponse();
        response.setSuccess(true);
        return response;
    }

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the connected property.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/connected")
    private ConnectedResponse processConnected(@RequestBody ConnectedMessage message) {
        // do something ...
        ConnectedResponse response = new ConnectedResponse();
        response.setSuccess(true);
        return response;
    }

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the facets characteristic.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/facets")
    private FacetsResponse processFacets(@RequestBody FacetsMessage message) {
        // do something ...
        FacetsResponse response = new FacetsResponse();
        response.setSuccess(true);
        response.setConfiguration(0);
        return response;
    }

    /**
     * Process messages from a TimeFlip device signaling
     * a change of the RSSI property.
     * 
     * @param message the message
     * @return the response
     */
    @PostMapping("/api/rssi")
    private RSSIResponse processRSSI(@RequestBody RSSIMessage message) {
        // do something ...
        RSSIResponse response = new RSSIResponse();
        response.setSuccess(true);
        return response;
    }

}

