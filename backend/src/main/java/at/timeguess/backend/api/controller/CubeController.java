package at.timeguess.backend.api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import at.timeguess.backend.model.api.BatteryLevelMessage;
import at.timeguess.backend.model.api.BatteryLevelResponse;
import at.timeguess.backend.model.api.ConnectedMessage;
import at.timeguess.backend.model.api.ConnectedResponse;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;
import at.timeguess.backend.model.api.RSSIMessage;
import at.timeguess.backend.model.api.RSSIResponse;

@RestController
public class CubeController {

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
