package at.timeguess.backend.model.api;

/**
 * A class that represents the messages sent by a TimeFlip device
 * signaling a change of the facets characteristic.
 */
public class FacetsMessage {

    private String identifier; // MAC address of the TimeFlip device
    private Integer configuration; // current value of the Calibration version characteristic
    private Integer facet; // new value of the facets characteristic

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Integer getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Integer configuration) {
        this.configuration = configuration;
    }

    public Integer getFacet() {
        return facet;
    }

    public void setFacet(Integer facet) {
        this.facet = facet;
    }

}
