package at.timeguess.backend.services;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;
import at.timeguess.backend.repositories.ConfigurationRepository;
import at.timeguess.backend.repositories.CubeFaceRepository;

/**
 * a service to manage {@link Configuration} entities
 *
 */
@Component
@Scope("application")
public class ConfigurationService {

    @Autowired
    ConfigurationRepository configurationRepository;

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

}
