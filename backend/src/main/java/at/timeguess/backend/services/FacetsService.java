package at.timeguess.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.FacetsEventPublisher;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;

@Service
public class FacetsService {

    @Autowired
    FacetsEventPublisher facetsEventPublisher;

    @Autowired
    CubeService cubeService;

    int dummyCubeFace = 0;

    public FacetsResponse processFacets(FacetsMessage message) {
        String identifier = message.getIdentifier();
        Integer calibrationVersion = message.getCalibrationVersion();
        int facet = message.getFacet();

        dummyCubeFace = facet % 12;

        if (cubeService.isMacAddressKnown(identifier)) {
            Cube cube = cubeService.findByMacAddress(identifier);
            facetsEventPublisher.publishFacetsEvent(cube, facet);
        }

        FacetsResponse response = new FacetsResponse();
        response.setCalibrationVersion(calibrationVersion);
        return response;
    }

    public int getDummyCubeFace() {
        return dummyCubeFace;
    }

    public void setDummyCubeFace(int dummyCubeFace) {
        this.dummyCubeFace = dummyCubeFace;
    }

}
