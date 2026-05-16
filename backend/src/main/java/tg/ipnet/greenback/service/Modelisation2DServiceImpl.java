package tg.ipnet.greenback.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ipnet.greenback.dto.ElementPlanCreationDto;
import tg.ipnet.greenback.dto.ElementPlanDto;
import tg.ipnet.greenback.dto.EstimationDto;
import tg.ipnet.greenback.dto.LigneEstimationDto;
import tg.ipnet.greenback.dto.Modelisation2DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_2DDto;
import tg.ipnet.greenback.dto.Modelisation_3DDto;
import tg.ipnet.greenback.entity.ElementPlan;
import tg.ipnet.greenback.entity.Estimation;
import tg.ipnet.greenback.entity.LigneEstimation;
import tg.ipnet.greenback.entity.Modelisation_2D;
import tg.ipnet.greenback.entity.Modelisation_3D;
import tg.ipnet.greenback.entity.Projet;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.repository.Modelisation2DRepository;
import tg.ipnet.greenback.repository.ProjetRepository;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.HistoryRepository;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.utils.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class Modelisation2DServiceImpl implements Modelisation2DService {

    private final Modelisation2DRepository modelisation2DRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    public Modelisation2DServiceImpl(
            Modelisation2DRepository modelisation2DRepository,
            ProjetRepository projetRepository,
            UserRepository userRepository,
            HistoryRepository historyRepository
    ) {
        this.modelisation2DRepository = modelisation2DRepository;
        this.projetRepository = projetRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public Modelisation_2DDto creerModelisation2D(Integer idProjet, Modelisation2DCreationDto demande) {
        User utilisateurCourant = getCurrentUser();
        Projet projet = getProjetAccessible(idProjet, utilisateurCourant);
        if (!peutCreerModelisation2D(projet, utilisateurCourant)) {
            throw new BadCredentialsException("Vous ne pouvez pas creer une modelisation 2D pour ce projet");
        }

        Modelisation_2D modelisation = new Modelisation_2D();
        modelisation.setNomModele(demande.getNomModele().trim());
        modelisation.setObjet(demande.getObjet().trim());
        modelisation.setDateCeation(LocalDateTime.now());
        modelisation.setProjet(projet);
        modelisation.setElements(mapElements(demande.getElements(), modelisation));

        Modelisation_2D modelisationSauvegardee = modelisation2DRepository.save(modelisation);
        createHistory(utilisateurCourant, "Creation d'une modelisation 2D sur le projet " + projet.getNomProjet());
        return mapperModelisation2D(modelisationSauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Modelisation_2DDto> listerModelisations2D(Integer idProjet) {
        Projet projet = getProjetAccessible(idProjet, getCurrentUser());
        return modelisation2DRepository.findByProjetIdOrderByDateCeationDesc(projet.getId())
                .stream()
                .map(this::mapperModelisation2D)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Modelisation_2DDto obtenirModelisation2D(Integer idProjet, Integer idModelisation2D) {
        getProjetAccessible(idProjet, getCurrentUser());
        Modelisation_2D modelisation = modelisation2DRepository.findByIdAndProjetId(idModelisation2D, idProjet)
                .orElseThrow(() -> new ResourceNotFoundException("Modelisation 2D introuvable"));
        return mapperModelisation2D(modelisation);
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

    private boolean peutCreerModelisation2D(Projet projet, User utilisateurCourant) {
        return Role.ADMIN.equals(utilisateurCourant.getRoles())
                || (Role.CLIENT.equals(utilisateurCourant.getRoles())
                && projet.getClient() != null
                && projet.getClient().getId().equals(utilisateurCourant.getId()))
                || (Role.ARCHITECTE.equals(utilisateurCourant.getRoles())
                && projet.getArchitecte() != null
                && projet.getArchitecte().getId().equals(utilisateurCourant.getId()));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Utilisateur non authentifie");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur connecte introuvable"));
    }

    private Modelisation_2DDto mapperModelisation2D(Modelisation_2D modelisation) {
        Modelisation_2DDto dto = new Modelisation_2DDto();
        dto.setId(modelisation.getId());
        dto.setNomModele(modelisation.getNomModele());
        dto.setDateCeation(modelisation.getDateCeation());
        dto.setObjet(modelisation.getObjet());
        if (modelisation.getProjet() != null) {
            dto.setIdProjet(modelisation.getProjet().getId());
        }
        dto.setElements(modelisation.getElements() == null
                ? List.of()
                : modelisation.getElements().stream().map(this::mapperElementPlan).toList());
        dto.setEstimations(modelisation.getEstimations() == null
                ? List.of()
                : modelisation.getEstimations().stream().map(this::mapperEstimation).toList());
        if (modelisation.getModelisation3D() != null) {
            dto.setModelisation3D(mapperModelisation3D(modelisation.getModelisation3D()));
        }
        return dto;
    }

    private ElementPlanDto mapperElementPlan(ElementPlan element) {
        ElementPlanDto dto = new ElementPlanDto();
        dto.setId(element.getId());
        dto.setLongeur(element.getLongeur());
        dto.setEpaisseur(element.getEpaisseur());
        dto.setType(element.getType());
        dto.setHauteur(element.getHauteur());
        dto.setPosition_X(element.getPosition_X());
        dto.setPosition_Y(element.getPosition_Y());
        dto.setRotation(element.getRotation());
        dto.setScaleX(element.getScaleX());
        dto.setScaleY(element.getScaleY());
        if (element.getModelisation2D() != null) {
            dto.setIdModelisation2D(element.getModelisation2D().getId());
        }
        return dto;
    }

    private EstimationDto mapperEstimation(Estimation estimation) {
        EstimationDto dto = new EstimationDto();
        dto.setId(estimation.getId());
        dto.setCouts(estimation.getCouts());
        dto.setDateEstimation(estimation.getDateEstimation());
        if (estimation.getModelisation2D() != null) {
            dto.setIdModelisation2D(estimation.getModelisation2D().getId());
        }
        dto.setLignes(estimation.getLignes() == null
                ? List.of()
                : estimation.getLignes().stream().map(this::mapperLigneEstimation).toList());
        return dto;
    }

    private LigneEstimationDto mapperLigneEstimation(LigneEstimation ligne) {
        LigneEstimationDto dto = new LigneEstimationDto();
        dto.setId(ligne.getId());
        dto.setQuantter(ligne.getQuantter());
        dto.setPrixTotal(ligne.getPrixTotal());
        dto.setLignes(List.of());
        return dto;
    }

    private Modelisation_3DDto mapperModelisation3D(Modelisation_3D modelisation3D) {
        Modelisation_3DDto dto = new Modelisation_3DDto();
        dto.setId(modelisation3D.getId());
        dto.setNomModel(modelisation3D.getNomModel());
        dto.setFormat(modelisation3D.getFormat());
        dto.setDateCreation(modelisation3D.getDateCreation());
        dto.setUrl_model(modelisation3D.getUrl_model());
        return dto;
    }

    private List<ElementPlan> mapElements(List<ElementPlanCreationDto> elements, Modelisation_2D modelisation) {
        if (elements == null || elements.isEmpty()) {
            return List.of();
        }

        return elements.stream()
                .map(elementDto -> {
                    ElementPlan element = new ElementPlan();
                    element.setLongeur(elementDto.getLongueur());
                    element.setEpaisseur(elementDto.getEpaisseur());
                    element.setType(elementDto.getType().trim());
                    element.setHauteur(elementDto.getHauteur());
                    element.setPosition_X(elementDto.getPositionX());
                    element.setPosition_Y(elementDto.getPositionY());
                    element.setRotation(elementDto.getRotation());
                    element.setScaleX(elementDto.getScaleX());
                    element.setScaleY(elementDto.getScaleY());
                    element.setModelisation2D(modelisation);
                    return element;
                })
                .toList();
    }

    private void createHistory(User user, String action) {
        History history = new History();
        history.setName(action);
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }
}
