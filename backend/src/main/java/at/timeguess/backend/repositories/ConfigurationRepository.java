package at.timeguess.backend.repositories;

import com.sun.istack.Nullable;

import at.timeguess.backend.model.Configuration;
import at.timeguess.backend.model.Cube;
import at.timeguess.backend.model.CubeFace;

public interface ConfigurationRepository extends AbstractRepository<Configuration, Long> {

	@Nullable
    public Configuration findByCube(Cube cube);

    public Configuration findByCubeface(CubeFace cubeFace);

    public Configuration findByFacet(Integer facet);
}
