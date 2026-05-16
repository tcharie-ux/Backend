package tg.ipnet.greenback.service;
 
import tg.ipnet.greenback.dto.NotificationCreationDTO;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.entity.Projet;
import tg.ipnet.greenback.security.model.User;

import java.util.List;

public interface NotificationService {
    NotificationDto envoyerNotification(NotificationCreationDTO demande);
    List<NotificationDto> listerNotificationsRecues();
    List<NotificationDto> listerNotificationsEnvoyees();
    NotificationDto accepterNotification(Long idNotification);
    NotificationDto accepterInvitationParToken(String tokenInvitation);
    NotificationDto consulterInvitation(String tokenInvitation);
    NotificationDto creerInvitationArchitecte(User emetteur, Projet projet, String emailArchitecte);
    NotificationDto creerInvitationArchitecte(User emetteur, Projet projet, String emailArchitecte, String message);
    User synchroniserInvitationsArchitecte(User user, String tokenInvitation);
}
