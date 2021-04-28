package at.timeguess.backend.model.api;

/**
 * A class that represents the messages from a Raspberry Pi
 * that has established a connection with a TimeFlip device
 * signaling a change of the facets characteristic.
 * <p>
 * Example:
 * <pre>
 * {
 *   "identifier": "56:23:89:34:56",
 *   "facet": 1
 * }
 * </pre>
 */
public class FacetsMessage {

    private String identifier; // MAC address of the TimeFlip device
    private int facet; // new value of the facets characteristic

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getFacet() {
        return facet;
    }

    public void setFacet(int facet) {
        this.facet = facet;
    }
    
}
