package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Utilisateur;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmailIgnoreCase(String email);
    Optional<Utilisateur> findByNomIgnoreCase(String nom);
    boolean existsByEmailIgnoreCase(String email);
}
