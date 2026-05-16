package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Projet;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Integer> {
    List<Projet> findByClientPublicIdOrderByDateCreationDesc(UUID clientId);
    List<Projet> findByArchitectePublicIdOrderByDateCreationDesc(UUID architecteId);
}
