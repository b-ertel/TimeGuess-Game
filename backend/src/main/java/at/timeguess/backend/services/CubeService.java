package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.FacetsEventPublisher;
import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.FacetsResponse;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.CubeFaceRepository;
import at.timeguess.backend.repositories.CubeRepository;

/**
 * A service for all kinds of stuff related to cubes, cube faces and configurations.
 * For everything else, have a look at the {@link StatusController}. :)
 */
@Service
public class CubeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CubeService.class);

    @Autowired
    private CubeRepository cubeRepo;
    @Autowired
    private CubeFaceRepository cubeFaceRepo;
    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private FacetsEventPublisher facetsEventPublisher;

    // to be removed ...
    int dummyCubeFace = 0;

    /**
     * @param cube: the cube to save
     */
    public Cube saveCube(Cube cube) {
        return cubeRepo.save(cube);
    }

    /**
     * method to create a new entry for a cube device in the database
     * 
     * @param message from the new Cube which contains attributes for the new Cube
     * @return new Cube
     */
    public Cube createCube(StatusMessage message) {
        Cube newCube = new Cube();
        newCube.setMacAddress(message.getIdentifier());
        newCube = saveCube(newCube);

        LOGGER.info("new Cube createt with mac {}", newCube.getMacAddress());

        return newCube;
    }

    /**
     * @return a list of all cubes 
     */
    public List<Cube> allCubes(){		
        return cubeRepo.findAll();
    }

    /**
     * @param cube to find out if mac address is already known
     * @return true if mac address is known, false otherwise
     */
    public boolean isMacAddressKnown(String macAddress) {
        if(cubeRepo.findByMacAddress(macAddress)==null){
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * find a cube by its mac address
     * 
     * @param identifier of cube
     * @return cube
     */
    public Cube getByMacAddress(String identifier) {
        return cubeRepo.findByMacAddress(identifier);
    }

    /** deletes a cube
     * @param cube to delete
     */
    public void deleteCube(Cube cube) {
        cubeRepo.delete(cube);

    }

    /**
     * @return a list of all CubeFace entities
     */
    public List<CubeFace> allCubeFaces(){
        return cubeFaceRepo.findAll();
    }

    /**
     * Checks if a given cube has already been configured.
     * 
     * @param cube the cube
     * @return a boolean indicating if the cube is configured
     */
    public boolean isConfigured(Cube cube){
        return !configurationRepository.findByCube(cube).isEmpty();
    }

    /**
     * Save a new set of new configurations for a given cube.
     * 
     * @param cube the cube
     * @param mapping a mapping of Cube faces to facet numbers
     */
    public void saveMappingForCube(Cube cube, Map<CubeFace, Integer> mapping) {
        // delete any existing configurations
        for (Configuration configuration : configurationRepository.findAll()) {
            configurationRepository.delete(configuration);
        }
        // create and save the new configurations
        for (Entry<CubeFace, Integer> entry : mapping.entrySet()) {
            Configuration configuration = new Configuration();
            configuration.setCube(cube);
            configuration.setCubeface(entry.getKey());
            configuration.setFacet(entry.getValue());
            configurationRepository.save(configuration);
        }
    }

    /**
     * Find a cube in the database by its MAC address.

     * 
     * @param macAddress the MAC address to search for
     * @return the cube with the given MAC address or null if not found (?)
     */
    public Cube findByMacAddress(String macAddress) {
        return cubeRepo.findByMacAddress(macAddress);
    }

    /**
     * Find a cube in the database by its id.
     * 
     * @param cubeId the id
     * @return (optionally) the cube with the given id
     */
    public Optional<Cube> findById(Long cubeId) {
        return cubeRepo.findById(cubeId);
    }

    /**
     * Process a message recieved through the REST API signaling a change
     * in the facets characteristic of a cube.
     *  
     * @param message the message
     * @return the response to the message
     */
    public FacetsResponse processFacets(FacetsMessage message) {
        String identifier = message.getIdentifier();
        // TODO calibration version
        Integer calibrationVersion = message.getCalibrationVersion();
        int facet = message.getFacet();

        // to be removed ...
        dummyCubeFace = facet % 12;

        if (isMacAddressKnown(identifier)) {
            Cube cube = findByMacAddress(identifier);
            facetsEventPublisher.publishFacetsEvent(cube, facet);
        }

        FacetsResponse response = new FacetsResponse();
        // TODO calibration version
        response.setCalibrationVersion(calibrationVersion);
        return response;
    }

    // to be removed ...
    public int getDummyCubeFace() {
        return dummyCubeFace;
    }

    // to be removed ...
    public void setDummyCubeFace(int dummyCubeFace) {
        this.dummyCubeFace = dummyCubeFace;
    }

}
