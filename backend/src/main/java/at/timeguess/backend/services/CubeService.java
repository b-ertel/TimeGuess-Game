package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import at.timeguess.backend.events.ConfiguredFacetsEventPublisher;
import at.timeguess.backend.events.UnconfiguredFacetsEventPublisher;
import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Threshold;
import at.timeguess.backend.model.ThresholdType;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.model.Interval;
import at.timeguess.backend.model.IntervalType;
import at.timeguess.backend.model.api.FacetsMessage;
import at.timeguess.backend.model.api.StatusMessage;
import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.IntervalRepository;
import at.timeguess.backend.repositories.ThresholdRepository;
import at.timeguess.backend.repositories.CubeFaceRepository;
import at.timeguess.backend.repositories.CubeRepository;

/**
 * A service for all kinds of stuff related to cubes, cube faces and configurations.
 * For everything else, check out the StatusController. :)
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
    private IntervalRepository intervalRepository;
    @Autowired
    private ThresholdRepository thresholdRepository;

    @Autowired
    private UnconfiguredFacetsEventPublisher unconfiguredFacetsEventPublisher;
    @Autowired
    private ConfiguredFacetsEventPublisher configuredFacetsEventPublisher;


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
     * Delete all existing configurations for a given cube.
     *  
     * @param cube the cube
     */
    public void deleteConfigurations(Cube cube) {
        for (Configuration configuration : configurationRepository.findByCube(cube)) {
            configurationRepository.delete(configuration);
        }
    }
        
    /**
     * Save a new set of new configurations for a given cube.
     * 
     * @param cube the cube
     * @param mapping a mapping of Cube faces to facet numbers
     */
    public void saveMappingForCube(Cube cube, Map<CubeFace, Integer> mapping) {
        deleteConfigurations(cube);
        for (Entry<CubeFace, Integer> entry : mapping.entrySet()) {
            Configuration configuration = new Configuration();
            configuration.setCube(cube);
            configuration.setCubeface(entry.getKey());
            configuration.setFacet(entry.getValue());
            configurationRepository.save(configuration);
        }
    }
    
    private CubeFace getMappedCubeFace(Cube cube, Integer facet) {
        List<Configuration> configurations = configurationRepository.findByCube(cube);
        for (Configuration configuration : configurations) {
            if (configuration.getFacet().equals(facet)) {
                return configuration.getCubeface();
            }
        }
        return null;
    }

    /**
     * Process a {@link FacetsMessage}.
     *  
     * @param message the message
     */
    public void processFacetsMessage(FacetsMessage message) {
        String identifier = message.getIdentifier();
        int facet = message.getFacet();
        if (isMacAddressKnown(identifier)) {
            Cube cube = getByMacAddress(identifier);
            CubeFace cubeFace = getMappedCubeFace(cube, facet);
            if (cubeFace != null) {
                configuredFacetsEventPublisher.publishConfiguredFacetsEvent(cube, cubeFace);
            }
            else {
                unconfiguredFacetsEventPublisher.publishUnconfiguredFacetsEvent(cube, facet);
            }
        }
    }

    /**
     * Query the value of a {@link Interval}.
     * 
     * @param type the type of the interval
     * @return the value of the interval
     */
    public int queryInterval(IntervalType type) {
        // the underlying assumption here is that the repository always contains such an object ...
        Interval interval = intervalRepository.findByType(type);
        return interval.getValue();
    }

    /**
     * Update the value of a {@link Interval}.
     * 
     * @param type the type of the interval
     * @param value the new value of the interval
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updateInterval(IntervalType type, int value) throws IllegalArgumentException {
        // the underlying assumption here is that the repository always contains such an object ...
        Interval interval = intervalRepository.findByType(type);
        if (value <= 0) {
            throw new IllegalArgumentException("The value of an interval has to be a positive integer!");
        }
        interval.setValue(value);
        intervalRepository.save(interval);
    }

    /**
     * Query the value of a {@link Threshold}.
     * 
     * @param type the type of the threshold
     * @return the value of the threshold
     */
    public int queryThreshold(ThresholdType type) {
        // the underlying assumption here is that the repository always contains such an object ...
        Threshold threshold = thresholdRepository.findByType(type);
        return threshold.getValue();
    }

    /**
     * Update the value of a {@link Threshold}.
     * 
     * @param type the type of the threshold
     * @param value the new value of the threshold
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void updateThreshold(ThresholdType type, int value) {
        // the underlying assumption here is that the repository always contains such an object ...
        Threshold threshold = thresholdRepository.findByType(type);
        threshold.setValue(value);
        thresholdRepository.save(threshold);
    }

}
