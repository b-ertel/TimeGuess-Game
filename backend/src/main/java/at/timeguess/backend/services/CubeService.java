package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
     * Save a new or existing cube to the database.
     * 
     * @param cube the cube to save
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('CUBE')")
    public Cube saveCube(Cube cube) throws IllegalArgumentException {
        try {
            return cubeRepo.save(cube);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("There was a problem saving the given cube, e.g., because a different cube with the same MAC address already exists in the database.");
        }
    }

    /**
     * @return a list of all cubes 
     */
    public List<Cube> allCubes(){		
        return cubeRepo.findAll();
    }
    
    /**
     * Check if a cube with a given MAC address exists in the database.
     * 
     * @param cube to find out if mac address is already known
     * @return true if mac address is known, false otherwise
     */
    public boolean isMacAddressKnown(String macAddress) {
        return cubeRepo.findByMacAddress(macAddress) != null;
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
    
    /**
     * Delete a given cube from the database.
     * 
     * @param cube the cube to delete
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteCube(Cube cube) throws IllegalArgumentException{
        try {
            cubeRepo.delete(cube);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("There was a problem deleting the given cube, e.g., because there are relationships involving the given cube.");
        }
    }

    /**
     * @return a list of all CubeFace entities
     */
    public List<CubeFace> allCubeFaces(){
        return cubeFaceRepo.findAll();
    }

    /**
     * Checks if a given cube has already been configured, i.e., if there
     * exist configurations for it.
     * <p>
     * Note that if this method returns true this does not necessarily mean
     * that the configuration actually works!
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
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('CUBE')")
    public void deleteConfigurations(Cube cube) {
        for (Configuration configuration : configurationRepository.findByCube(cube)) {
            configurationRepository.delete(configuration);
        }
    }
    
    /**
     * Save a new set of new configurations for a given cube.
     * <p>
     * Note that the correctness of the mapping is not checked, so the
     * fact that this method does not throw an exception does not mean that
     * the configuration will actually work!
     * 
     * @param cube the cube
     * @param mapping a mapping of Cube faces to facet numbers
     * @throws IllegalArgumentException
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    public void saveMappingForCube(Cube cube, Map<CubeFace, Integer> mapping) throws IllegalArgumentException {
        if (!isMacAddressKnown(cube.getMacAddress())) {
            throw new IllegalArgumentException("The given cube does not exist!");
        }
        if (isConfigured(cube)) {
            throw new IllegalArgumentException("An existing configuration already exists for the given cube and has to be deleted first!");
        }
        for (Entry<CubeFace, Integer> entry : mapping.entrySet()) {
            Configuration configuration = new Configuration();
            configuration.setCube(cube);
            configuration.setCubeface(entry.getKey());
            configuration.setFacet(entry.getValue());
            configurationRepository.save(configuration);
        }
    }
    
    /**
     * Find the mapped cube face for a given cube and facet number. 
     * 
     * @param cube the cube
     * @param facet the facet number
     * @return the mapped cube face if this uniquely (!) exists or null otherwise
     */
    public CubeFace getMappedCubeFace(Cube cube, Integer facet) {
        List<Configuration> matchingConfigurations = configurationRepository.findByCube(cube).stream().filter(c -> c.getFacet() == facet).collect(Collectors.toList());
        if (matchingConfigurations.size() == 1) {
            return matchingConfigurations.get(0).getCubeface();
        }
        return null;
    }

    /**
     * Find the mapped facet number for a given cube and cube face. 
     * 
     * @param cube the cube
     * @param cubeFace the cube face
     * @return the mapped facet number if this uniquely (!) exists or null otherwise
     */
    public Integer getMappedFacet(Cube cube, CubeFace cubeFace) {
        List<Configuration> matchingConfigurations = configurationRepository.findByCube(cube).stream().filter(c -> c.getCubeface() == cubeFace).collect(Collectors.toList());
        if (matchingConfigurations.size() == 1) {
            return matchingConfigurations.get(0).getFacet();
        }
        return null;
    }

    /**
     * Process a {@link FacetsMessage}.
     *  
     * @param message the message
     */
    @PreAuthorize("hasAuthority('CUBE')")
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
