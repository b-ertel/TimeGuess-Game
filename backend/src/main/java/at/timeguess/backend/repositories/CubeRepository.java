package at.timeguess.backend.repositories;

import java.util.Optional;

import at.timeguess.backend.model.Cube;

public interface CubeRepository extends AbstractRepository<Cube, Long> {

    Optional<Cube> findById(Long id);

    Cube findByMacAddress(String macAddress);

}
