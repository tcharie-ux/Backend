package tg.ipnet.greenback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tg.ipnet.greenback.entity.Notification;
import tg.ipnet.greenback.enums.StatutNotification;
import tg.ipnet.greenback.enums.TypeNotification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireUsernameOrEmailDestinataireIgnoreCaseOrderByDateEnvoieDesc(String username, String emailDestinataire);
    List<Notification> findByEmetteurUsernameOrderByDateEnvoieDesc(String username);
    Optional<Notification> findByTokenInvitation(String tokenInvitation);
    List<Notification> findByTypeNotificationAndEmailDestinataireIgnoreCaseAndStatut(
            TypeNotification typeNotification,
            String emailDestinataire,
            StatutNotification statut
    );
}
