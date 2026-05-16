package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Modelisation_3D;

import java.util.Optional;

@Repository
public interface Modelisation3DRepository extends JpaRepository<Modelisation_3D, Integer> {
    Optional<Modelisation_3D> findByModelisation2DId(Integer modelisation2DId);
}
