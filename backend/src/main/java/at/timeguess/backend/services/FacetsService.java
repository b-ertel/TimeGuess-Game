package at.timeguess.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.FacetsEventPublisher;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;

@Service
public class FacetsService {

    private int dummyCubeFace = 0;

    @Autowired
    FacetsEventPublisher facetsEventPublisher;

    public FacetsResponse processFacets(FacetsMessage message) {
        String identifier = message.getIdentifier();
        Integer configuration = message.getConfiguration();
        Integer facet = message.getFacet();

        // update `dummyCubeFace` and notify event listeners of changed value
        dummyCubeFace = facet % 12;
        facetsEventPublisher.publishFacetsEvent();

        FacetsResponse response = new FacetsResponse();
        response.setSuccess(true);
        response.setConfiguration(0);
        return response;
    }

    public int getDummyCubeFace() {
        return dummyCubeFace;
    }

    public void setDummyCubeFace(int dummyCubeFace) {
        this.dummyCubeFace = dummyCubeFace;
    }

}
