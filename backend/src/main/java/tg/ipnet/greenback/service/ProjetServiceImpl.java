package tg.ipnet.greenback.service;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tg.ipnet.greenback.dto.ArchitectureDto;
import tg.ipnet.greenback.dto.NotificationDto;
import tg.ipnet.greenback.dto.ProjetCreationDto;
import tg.ipnet.greenback.dto.ProjetDto;
import tg.ipnet.greenback.entity.Architecture;
import tg.ipnet.greenback.entity.Projet;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.repository.ArchitectureRepository;
import tg.ipnet.greenback.repository.ProjetRepository;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.HistoryRepository;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.utils.ResourceNotFoundException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProjetServiceImpl implements ProjetService {

    private final ProjetRepository projetRepository;
    private final ArchitectureRepository architectureRepository;
    private final Modelisation2DService modelisation2DService;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final NotificationService notificationService;

    public ProjetServiceImpl(
            ProjetRepository projetRepository,
            ArchitectureRepository architectureRepository,
            Modelisation2DService modelisation2DService,
            UserRepository userRepository,
            HistoryRepository historyRepository,
            NotificationService notificationService
    ) {
        this.projetRepository = projetRepository;
        this.architectureRepository = architectureRepository;
        this.modelisation2DService = modelisation2DService;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ProjetDto creerProjet(ProjetCreationDto demande) {
        User client = getCurrentUser();
        if (!Role.CLIENT.equals(client.getRoles()) && !Role.ADMIN.equals(client.getRoles())) {
            throw new BadCredentialsException("Seul un client ou un admin peut creer un projet");
        }

        Projet projet = new Projet();
        projet.setNomProjet(demande.getNomProjet());
        projet.setDescription(demande.getDescription());
        projet.setStatut(false);
        projet.setDateCreation(demande.getDateCreation());
        projet.setClient(client);
        projet.setEmailArchitecteInvite(normalizeEmail(demande.getEmailArchitecte()));

        Projet projetSauvegarde = projetRepository.save(projet);
        NotificationDto invitation = null;
        if (projetSauvegarde.getEmailArchitecteInvite() != null) {
            invitation = notificationService.creerInvitationArchitecte(
                    client,
                    projetSauvegarde,
                    projetSauvegarde.getEmailArchitecteInvite()
            );
        }

        createHistory(client, "Creation du projet " + projetSauvegarde.getNomProjet());
        return mapperProjet(projetSauvegarde, invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDto> listerMesProjets() {
        User utilisateurCourant = getCurrentUser();
        List<Projet> projets = new ArrayList<>();
        if (Role.ADMIN.equals(utilisateurCourant.getRoles())) {
            projets = projetRepository.findAll()
                    .stream()
                    .sorted((a, b) -> b.getDateCreation().compareTo(a.getDateCreation()))
                    .toList();
        } else if (Role.CLIENT.equals(utilisateurCourant.getRoles())) {
            projets = projetRepository.findByClientPublicIdOrderByDateCreationDesc(utilisateurCourant.getPublicId());
        } else if (Role.ARCHITECTE.equals(utilisateurCourant.getRoles())) {
            projets = projetRepository.findByArchitectePublicIdOrderByDateCreationDesc(utilisateurCourant.getPublicId());
        }

        return projets.stream()
                .map(projet -> mapperProjet(projet, null))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetDto obtenirProjet(Integer idProjet) {
        Projet projet = getProjetAccessible(idProjet, getCurrentUser());
        return mapperProjet(projet, null);
    }

    @Override
    public ArchitectureDto ajouterEsquisse(Integer idProjet, MultipartFile fichier) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de l'esquisse est obligatoire");
        }

        User utilisateurCourant = getCurrentUser();
        Projet projet = getProjetAccessible(idProjet, utilisateurCourant);
        if (!peutModifierEsquisse(projet, utilisateurCourant)) {
            throw new BadCredentialsException("Vous ne pouvez pas deposer une esquisse pour ce projet");
        }

        Architecture architecture = new Architecture();
        architecture.setFileName(fichier.getOriginalFilename());
        architecture.setFileType(resolveContentType(fichier.getContentType()));
        architecture.setTaille(fichier.getSize());
        architecture.setDateDepot(LocalDateTime.now());
        architecture.setAuteur(utilisateurCourant);
        architecture.setProjet(projet);

        try {
            architecture.setData(fichier.getBytes());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Impossible de lire le fichier fourni", ex);
        }

        Architecture architectureSauvegardee = architectureRepository.save(architecture);
        createHistory(utilisateurCourant, "Depot d'une esquisse sur le projet " + projet.getNomProjet());
        return mapperArchitecture(architectureSauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchitectureDto> listerEsquisses(Integer idProjet) {
        Projet projet = getProjetAccessible(idProjet, getCurrentUser());
        return architectureRepository.findByProjetIdOrderByDateDepotDesc(projet.getId())
                .stream()
                .map(this::mapperArchitecture)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Architecture chargerEsquisse(Integer idProjet, Integer idArchitecture) {
        User utilisateurCourant = getCurrentUser();
        Projet projet = getProjetAccessible(idProjet, utilisateurCourant);

        Architecture architecture = architectureRepository.findById(idArchitecture)
                .orElseThrow(() -> new ResourceNotFoundException("Esquisse introuvable"));

        if (architecture.getProjet().getId() != projet.getId()) {
            throw new ResourceNotFoundException("Cette esquisse n'appartient pas au projet demande");
        }

        return architecture;
    }

    private Projet getProjetAccessible(Integer idProjet, User utilisateurCourant) {
        Projet projet = projetRepository.findById(idProjet)
                .orElseThrow(() -> new ResourceNotFoundException("Projet introuvable"));

        boolean estAdmin = Role.ADMIN.equals(utilisateurCourant.getRoles());
        boolean estClient = projet.getClient() != null
                && projet.getClient().getPublicId() != null
                && projet.getClient().getPublicId().equals(utilisateurCourant.getPublicId());
        boolean estArchitecte = projet.getArchitecte() != null
                && projet.getArchitecte().getPublicId() != null
                && projet.getArchitecte().getPublicId().equals(utilisateurCourant.getPublicId());

        if (!estAdmin && !estClient && !estArchitecte) {
            throw new BadCredentialsException("Vous ne pouvez pas acceder a ce projet");
        }

        return projet;
    }

    private boolean peutModifierEsquisse(Projet projet, User utilisateurCourant) {
        return Role.ADMIN.equals(utilisateurCourant.getRoles())
                || (projet.getClient() != null && projet.getClient().getId().equals(utilisateurCourant.getId()))
                || (projet.getArchitecte() != null && projet.getArchitecte().getId().equals(utilisateurCourant.getId()));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Utilisateur non authentifie");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur connecte introuvable"));
    }

    private ProjetDto mapperProjet(Projet projet, NotificationDto invitation) {
        ProjetDto dto = new ProjetDto();
        dto.setId(projet.getId());
        dto.setDescription(projet.getDescription());
        dto.setNomProjet(projet.getNomProjet());
        dto.setStatut(projet.isStatut());
        dto.setDateCreation(projet.getDateCreation());

        if (projet.getClient() != null) {
            dto.setIdClient(projet.getClient().getPublicId());
            dto.setNomClient(projet.getClient().getNom());
        }

        if (projet.getArchitecte() != null) {
            dto.setIdArchitecte(projet.getArchitecte().getPublicId());
            dto.setNomArchitecte(projet.getArchitecte().getNom());
        }

        dto.setEmailArchitecteInvite(projet.getEmailArchitecteInvite());
        if (invitation != null) {
            dto.setIdNotificationInvitationArchitecte(invitation.getIdNotification());
            dto.setTokenInvitationArchitecte(invitation.getTokenInvitation());
        }

        dto.setArchitectures(architectureRepository.findByProjetIdOrderByDateDepotDesc(projet.getId())
                .stream()
                .map(this::mapperArchitecture)
                .toList());
        dto.setModeles2D(modelisation2DService.listerModelisations2D(projet.getId()));
        return dto;
    }

    private ArchitectureDto mapperArchitecture(Architecture architecture) {
        ArchitectureDto dto = new ArchitectureDto();
        dto.setId(architecture.getId());
        dto.setFileName(architecture.getFileName());
        dto.setFileType(architecture.getFileType());
        dto.setTaille(architecture.getTaille());
        dto.setDateDepot(architecture.getDateDepot());
        if (architecture.getAuteur() != null) {
            dto.setIdAuteur(architecture.getAuteur().getPublicId());
            dto.setNomAuteur(architecture.getAuteur().getNom());
        }
        return dto;
    }

    private void createHistory(User user, String action) {
        History history = new History();
        history.setName(action);
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String resolveContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentType;
    }
}
