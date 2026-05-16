package tg.ipnet.greenback.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.security.model.EmailVerification;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailIgnoreCaseAndCodeAndUsedFalseOrderByCreatedAtDesc(String email, String code);
}
