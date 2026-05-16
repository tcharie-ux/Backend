package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Architecture;

import java.util.List;

@Repository
public interface ArchitectureRepository extends JpaRepository<Architecture, Integer> {
    List<Architecture> findByProjetIdOrderByDateDepotDesc(Integer projetId);
}
