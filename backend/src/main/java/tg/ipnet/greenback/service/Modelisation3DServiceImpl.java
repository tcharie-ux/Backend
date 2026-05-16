package tg.ipnet.greenback.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ipnet.greenback.dto.Modelisation3DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_3DDto;
import tg.ipnet.greenback.entity.Modelisation_2D;
import tg.ipnet.greenback.entity.Modelisation_3D;
import tg.ipnet.greenback.entity.Projet;
import tg.ipnet.greenback.enums.Role;
import tg.ipnet.greenback.repository.Modelisation2DRepository;
import tg.ipnet.greenback.repository.Modelisation3DRepository;
import tg.ipnet.greenback.security.model.History;
import tg.ipnet.greenback.security.model.User;
import tg.ipnet.greenback.security.repository.HistoryRepository;
import tg.ipnet.greenback.security.repository.UserRepository;
import tg.ipnet.greenback.utils.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
public class Modelisation3DServiceImpl implements Modelisation3DService {
    private final Modelisation2DRepository modelisation2DRepository;
    private final Modelisation3DRepository modelisation3DRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    public Modelisation3DServiceImpl(
            Modelisation2DRepository modelisation2DRepository,
            Modelisation3DRepository modelisation3DRepository,
            UserRepository userRepository,
            HistoryRepository historyRepository
    ) {
        this.modelisation2DRepository = modelisation2DRepository;
        this.modelisation3DRepository = modelisation3DRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public Modelisation_3DDto sauvegarderModelisation3D(Integer idProjet, Integer idModelisation2D, Modelisation3DCreationDto demande) {
        User utilisateurCourant = getCurrentUser();
        Modelisation_2D modelisation2D = getModelisation2DAccessible(idProjet, idModelisation2D, utilisateurCourant);

        Modelisation_3D modelisation3D = modelisation3DRepository.findByModelisation2DId(idModelisation2D)
                .orElseGet(Modelisation_3D::new);
        modelisation3D.setNomModel(demande.getNomModel().trim());
        modelisation3D.setFormat(demande.getFormat());
        modelisation3D.setDateCreation(LocalDateTime.now());
        modelisation3D.setUrl_model(demande.getUrl_model());
        modelisation3D.setModelisation2D(modelisation2D);
        modelisation2D.setModelisation3D(modelisation3D);

        Modelisation_3D sauvegardee = modelisation3DRepository.save(modelisation3D);
        createHistory(utilisateurCourant, "Sauvegarde 3D du projet " + modelisation2D.getProjet().getNomProjet());
        return mapperModelisation3D(sauvegardee);
    }

    @Override
    @Transactional(readOnly = true)
    public Modelisation_3DDto obtenirModelisation3D(Integer idProjet, Integer idModelisation2D) {
        getModelisation2DAccessible(idProjet, idModelisation2D, getCurrentUser());
        return modelisation3DRepository.findByModelisation2DId(idModelisation2D)
                .map(this::mapperModelisation3D)
                .orElseThrow(() -> new ResourceNotFoundException("Modelisation 3D introuvable"));
    }

    private Modelisation_2D getModelisation2DAccessible(Integer idProjet, Integer idModelisation2D, User utilisateurCourant) {
        Modelisation_2D modelisation2D = modelisation2DRepository.findByIdAndProjetId(idModelisation2D, idProjet)
                .orElseThrow(() -> new ResourceNotFoundException("Modelisation 2D introuvable"));
        Projet projet = modelisation2D.getProjet();

        boolean estAdmin = Role.ADMIN.equals(utilisateurCourant.getRoles());
        boolean estClient = projet.getClient() != null
                && projet.getClient().getPublicId() != null
                && projet.getClient().getPublicId().equals(utilisateurCourant.getPublicId());
        boolean estArchitecte = projet.getArchitecte() != null
                && projet.getArchitecte().getPublicId() != null
                && projet.getArchitecte().getPublicId().equals(utilisateurCourant.getPublicId());

        if (!estAdmin && !estClient && !estArchitecte) {
            throw new BadCredentialsException("Vous ne pouvez pas acceder a cette modelisation 3D");
        }

        return modelisation2D;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadCredentialsException("Utilisateur non authentifie");
        }

        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur connecte introuvable"));
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

    private void createHistory(User user, String action) {
        History history = new History();
        history.setName(action);
        history.setUser(user);
        history.setDateHistory(new Date());
        historyRepository.save(history);
    }
}
