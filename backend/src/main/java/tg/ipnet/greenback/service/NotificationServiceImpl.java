package tg.ipnet.greenback.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ipnet.greenback.dto.NotificationCreationDTO;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.entity.Notification;
import tg.ipnet.greenback.entity.Projet;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.enums.StatutNotification;
import tg.ipnet.greenback.enums.TypeNotification;
import tg.ipnet.greenback.repository.NotificationRepository;
import tg.ipnet.greenback.repository.ProjetRepository;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.HistoryRepository;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.utils.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final EmailService emailService;
    private final String frontendUrl;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            ProjetRepository projetRepository,
            UserRepository userRepository,
            HistoryRepository historyRepository,
            EmailService emailService,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.notificationRepository = notificationRepository;
        this.projetRepository = projetRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public NotificationDto envoyerNotification(NotificationCreationDTO demande) {
        User emetteur = getCurrentUser();
        TypeNotification typeNotification = resolveType(demande.getTypeNotification());

        if (TypeNotification.INVITATION_ARCHITECTE.equals(typeNotification)) {
            if (!Role.CLIENT.equals(emetteur.getRoles()) && !isAdmin(emetteur)) {
                throw new BadCredentialsException("Seul un client ou un admin peut inviter un architecte");
            }
            if (demande.getIdProjet() == null) {
                throw new IllegalArgumentException("Le projet est obligatoire pour une invitation architecte");
            }
            if (demande.getEmailDestinataire() == null || demande.getEmailDestinataire().isBlank()) {
                throw new IllegalArgumentException("L'email de l'architecte est obligatoire");
            }

            Projet projet = projetRepository.findById(demande.getIdProjet())
                    .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));
            verifyProjectOwnership(emetteur, projet);
            return creerInvitationArchitecte(emetteur, projet, demande.getEmailDestinataire(), demande.getMessage());
        }

        if (!isArchitectOrAdmin(emetteur)) {
            throw new BadCredentialsException("Seul un architecte ou un admin peut envoyer une notification");
        }

        if (demande.getIdDestinataire() == null) {
            throw new IllegalArgumentException("Le destinataire est obligatoire");
        }

        User destinataire = userRepository.findByPublicId(demande.getIdDestinataire())
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire introuvable"));

        Notification notification = new Notification();
        notification.setMessage(demande.getMessage());
        notification.setStatut(StatutNotification.EN_ATTENTE);
        notification.setTypeNotification(TypeNotification.MESSAGE);
        notification.setDateEnvoie(LocalDateTime.now());
        notification.setEmetteur(emetteur);
        notification.setDestinataire(destinataire);

        Notification notificationSauvegardee = notificationRepository.save(notification);
        createHistory(emetteur, "Notification envoyee a " + destinataire.getUsername());
        return mapper(notificationSauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> listerNotificationsRecues() {
        User utilisateurCourant = getCurrentUser();
        return notificationRepository
                .findByDestinataireUsernameOrEmailDestinataireIgnoreCaseOrderByDateEnvoieDesc(
                        utilisateurCourant.getUsername(),
                        utilisateurCourant.getUsername()
                )
                .stream()
                .map(this::mapper)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> listerNotificationsEnvoyees() {
        User utilisateurCourant = getCurrentUser();
        return notificationRepository.findByEmetteurUsernameOrderByDateEnvoieDesc(utilisateurCourant.getUsername())
                .stream()
                .map(this::mapper)
                .toList();
    }

    @Override
    public NotificationDto accepterNotification(Long idNotification) {
        User utilisateurCourant = getCurrentUser();
        Notification notification = notificationRepository.findById(idNotification)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable"));

        if (TypeNotification.INVITATION_ARCHITECTE.equals(notificationTypeOf(notification))) {
            verifyInvitationAcceptanceAccess(notification, utilisateurCourant);
            notification.setStatut(StatutNotification.ACCEPTEE);
            notification.setDateReponse(LocalDateTime.now());
            notification.setDestinataire(utilisateurCourant);
            Notification notificationSauvegardee = notificationRepository.save(notification);
            synchroniserInvitationsArchitecte(utilisateurCourant, notification.getTokenInvitation());
            createHistory(utilisateurCourant, "Invitation architecte acceptee #" + idNotification);
            return mapper(notificationSauvegardee);
        }

        boolean estDestinataire = notification.getDestinataire() != null
                && notification.getDestinataire().getId().equals(utilisateurCourant.getId());
        if (!estDestinataire && !isAdmin(utilisateurCourant)) {
            throw new BadCredentialsException("Vous ne pouvez pas accepter cette notification");
        }

        notification.setStatut(StatutNotification.ACCEPTEE);
        notification.setDateReponse(LocalDateTime.now());

        Notification notificationSauvegardee = notificationRepository.save(notification);
        createHistory(utilisateurCourant, "Notification acceptee #" + idNotification);
        return mapper(notificationSauvegardee);
    }

    @Override
    public NotificationDto accepterInvitationParToken(String tokenInvitation) {
        Notification notification = getInvitationByToken(tokenInvitation);
        if (!TypeNotification.INVITATION_ARCHITECTE.equals(notificationTypeOf(notification))) {
            throw new IllegalArgumentException("Cette invitation n'est pas une invitation architecte");
        }
        if (StatutNotification.REJETEE.equals(notification.getStatut())) {
            throw new IllegalArgumentException("Cette invitation a deja ete rejetee");
        }

        notification.setStatut(StatutNotification.ACCEPTEE);
        notification.setDateReponse(LocalDateTime.now());
        Notification notificationSauvegardee = notificationRepository.save(notification);
        return mapper(notificationSauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDto consulterInvitation(String tokenInvitation) {
        return mapper(getInvitationByToken(tokenInvitation));
    }

    @Override
    public NotificationDto creerInvitationArchitecte(User emetteur, Projet projet, String emailArchitecte) {
        return creerInvitationArchitecte(emetteur, projet, emailArchitecte, null);
    }

    @Override
    public NotificationDto creerInvitationArchitecte(User emetteur, Projet projet, String emailArchitecte, String messagePersonnalise) {
        String emailNormalise = normalizeEmail(emailArchitecte);
        if (emailNormalise == null) {
            throw new IllegalArgumentException("L'email de l'architecte est obligatoire");
        }

        Notification notification = new Notification();
        notification.setMessage(messagePersonnalise == null || messagePersonnalise.isBlank()
                ? "Invitation a participer au projet " + projet.getNomProjet()
                : messagePersonnalise);
        notification.setStatut(StatutNotification.EN_ATTENTE);
        notification.setTypeNotification(TypeNotification.INVITATION_ARCHITECTE);
        notification.setDateEnvoie(LocalDateTime.now());
        notification.setEmetteur(emetteur);
        notification.setEmailDestinataire(emailNormalise);
        notification.setTokenInvitation(UUID.randomUUID().toString());
        notification.setProjet(projet);

        userRepository.findByUsername(emailNormalise).ifPresent(notification::setDestinataire);

        Notification notificationSauvegardee = notificationRepository.save(notification);
        projet.setEmailArchitecteInvite(emailNormalise);
        projetRepository.save(projet);
        String invitationLink = frontendUrl + "/register-architect?token=" + notificationSauvegardee.getTokenInvitation();
        emailService.sendArchitectInvitation(
                emailNormalise,
                invitationLink,
                projet.getNomProjet(),
                notificationSauvegardee.getMessage()
        );
        createHistory(emetteur, "Invitation architecte envoyee pour le projet " + projet.getNomProjet());
        return mapper(notificationSauvegardee);
    }

    @Override
    public User synchroniserInvitationsArchitecte(User user, String tokenInvitation) {
        String emailUtilisateur = normalizeEmail(user.getUsername());
        List<Notification> invitations = notificationRepository
                .findByTypeNotificationAndEmailDestinataireIgnoreCaseAndStatut(
                        TypeNotification.INVITATION_ARCHITECTE,
                        emailUtilisateur,
                        StatutNotification.ACCEPTEE
                );

        if (tokenInvitation != null && !tokenInvitation.isBlank()) {
            Notification notification = getInvitationByToken(tokenInvitation);
            if (TypeNotification.INVITATION_ARCHITECTE.equals(notificationTypeOf(notification))
                    && StatutNotification.ACCEPTEE.equals(notification.getStatut())
                    && emailMatches(notification.getEmailDestinataire(), user.getUsername())
                    && invitations.stream().noneMatch(item -> item.getIdNotification().equals(notification.getIdNotification()))) {
                invitations.add(notification);
            }
        }

        boolean roleMisAJour = false;
        for (Notification invitation : invitations) {
            if (invitation.getDestinataire() == null) {
                invitation.setDestinataire(user);
            }

            Projet projet = invitation.getProjet();
            if (projet != null) {
                projet.setArchitecte(user);
                projet.setStatut(true);
                projet.setEmailArchitecteInvite(normalizeEmail(user.getUsername()));
                projetRepository.save(projet);
            }
            notificationRepository.save(invitation);
            roleMisAJour = true;
        }

        if (roleMisAJour && !Role.ADMIN.equals(user.getRoles()) && !Role.ARCHITECTE.equals(user.getRoles())) {
            user.setRoles(Role.ARCHITECTE);
            user = userRepository.save(user);
        }

        return user;
    }

    private Notification getInvitationByToken(String tokenInvitation) {
        if (tokenInvitation == null || tokenInvitation.isBlank()) {
            throw new IllegalArgumentException("Le token d'invitation est obligatoire");
        }

        return notificationRepository.findByTokenInvitation(tokenInvitation)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation introuvable"));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Utilisateur non authentifie");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur connecte introuvable"));
    }

    private void verifyProjectOwnership(User emetteur, Projet projet) {
        if (isAdmin(emetteur)) {
            return;
        }

        boolean estClientDuProjet = projet.getClient() != null
                && projet.getClient().getPublicId() != null
                && projet.getClient().getPublicId().equals(emetteur.getPublicId());
        if (!estClientDuProjet) {
            throw new BadCredentialsException("Vous ne pouvez pas inviter un architecte pour ce projet");
        }
    }

    private void verifyInvitationAcceptanceAccess(Notification notification, User utilisateurCourant) {
        if (isAdmin(utilisateurCourant)) {
            return;
        }

        boolean emailCorrespond = emailMatches(notification.getEmailDestinataire(), utilisateurCourant.getUsername());
        boolean estDestinataireReference = notification.getDestinataire() != null
                && notification.getDestinataire().getPublicId() != null
                && notification.getDestinataire().getPublicId().equals(utilisateurCourant.getPublicId());
        if (!emailCorrespond && !estDestinataireReference) {
            throw new BadCredentialsException("Vous ne pouvez pas accepter cette invitation");
        }
    }

    private boolean emailMatches(String emailInvitation, String username) {
        return normalizeEmail(emailInvitation) != null
                && normalizeEmail(emailInvitation).equals(normalizeEmail(username));
    }

    private TypeNotification resolveType(String typeNotification) {
        if (typeNotification == null || typeNotification.isBlank()) {
            return TypeNotification.MESSAGE;
        }
        return TypeNotification.valueOf(typeNotification.trim().toUpperCase(Locale.ROOT));
    }

    private boolean isArchitectOrAdmin(User user) {
        return Role.ARCHITECTE.equals(user.getRoles()) || isAdmin(user);
    }

    private boolean isAdmin(User user) {
        return Role.ADMIN.equals(user.getRoles());
    }

    private void createHistory(User user, String action) {
        History history = new History();
        history.setName(action);
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }

    private NotificationDto mapper(Notification notificationEntity) {
        NotificationDto notification = new NotificationDto();
        notification.setIdNotification(notificationEntity.getIdNotification());
        notification.setMessage(notificationEntity.getMessage());
        notification.setStatut(notificationEntity.getStatut().name());
        notification.setTypeNotification(notificationTypeOf(notificationEntity).name());
        notification.setIdEmetteur(notificationEntity.getEmetteur().getPublicId());
        notification.setNomEmetteur(notificationEntity.getEmetteur().getUsername());
        if (notificationEntity.getDestinataire() != null) {
            notification.setIdDestinataire(notificationEntity.getDestinataire().getPublicId());
            notification.setNomDestinataire(notificationEntity.getDestinataire().getUsername());
        }
        notification.setEmailDestinataire(notificationEntity.getEmailDestinataire());
        notification.setTokenInvitation(notificationEntity.getTokenInvitation());
        if (notificationEntity.getProjet() != null) {
            notification.setIdProjet(notificationEntity.getProjet().getId());
            notification.setNomProjet(notificationEntity.getProjet().getNomProjet());
        }
        notification.setDateEnvoie(notificationEntity.getDateEnvoie());
        notification.setDateReponse(notificationEntity.getDateReponse());
        return notification;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private TypeNotification notificationTypeOf(Notification notification) {
        if (notification.getTypeNotification() == null) {
            return TypeNotification.MESSAGE;
        }
        return notification.getTypeNotification();
    }
}
