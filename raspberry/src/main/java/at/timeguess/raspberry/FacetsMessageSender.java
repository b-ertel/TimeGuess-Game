package at.timeguess.raspberry;

import java.util.logging.*;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import tinyb.BluetoothNotification;

/**
 * A BluetoothNotification sending facets messages to the backend
 * whenever a notification about a change of the facets characteristic is issued.
 */
public class FacetsMessageSender implements BluetoothNotification<byte[]> {

    private static final String API_PATH = "/api/facets";

    private final Logger logger = Logger.getLogger("at.timeguess.raspberry");

    private final String timeflipMacAddress;
    private final String backendUrl;

    public FacetsMessageSender(String timeflipMacAddress, String backendUrl) {
        this.timeflipMacAddress = timeflipMacAddress;
        this.backendUrl = backendUrl;
    }

    @SuppressWarnings("unchecked")
    public void run(byte[] facetRaw) {
        try {
            logger.info("Sending facets message to backend ...");
            String body = String.format("{\"identifier\": \"%s\", \"facet\": %d}",
                    timeflipMacAddress,
                    facetRaw[0]);
            Unirest.post(backendUrl + API_PATH)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .asEmpty()
                    .ifSuccess(response -> {
                        logger.info("Facets message successfully processed by backend.");
                    })
                    .ifFailure(response -> {
                        logger.warning("Facets message not successfully processed by backend!");
                    });
        }
        catch (UnirestException e) {
            logger.warning("Could not connect to backend!");
        }
    }

}
 