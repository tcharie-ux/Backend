package tg.ipnet.greenback.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.security.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
