package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Modelisation_2D;

import java.util.List;
import java.util.Optional;

@Repository
public interface Modelisation2DRepository extends JpaRepository<Modelisation_2D, Integer> {
    List<Modelisation_2D> findByProjetIdOrderByDateCeationDesc(Integer projetId);
    Optional<Modelisation_2D> findByIdAndProjetId(Integer id, Integer projetId);
}
