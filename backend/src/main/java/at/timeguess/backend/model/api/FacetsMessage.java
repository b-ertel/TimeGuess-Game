package at.timeguess.backend.model.api;

/**
 * A class for facets messages.
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
