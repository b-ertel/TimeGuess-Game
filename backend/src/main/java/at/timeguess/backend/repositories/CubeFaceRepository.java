package at.timeguess.backend.repositories;

import java.util.Optional;

import at.timeguess.backend.model.CubeFace;

public interface CubeFaceRepository extends AbstractRepository<CubeFace, String> {

    Optional<CubeFace> findById(String id);

}
