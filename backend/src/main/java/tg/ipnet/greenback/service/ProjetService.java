package tg.ipnet.greenback.service;

import org.springframework.web.multipart.MultipartFile;
import tg.ipnet.greenback.dto.ArchitectureDto;
import tg.ipnet.greenback.dto.ProjetCreationDto;
import tg.ipnet.greenback.dto.ProjetDto;
import tg.ipnet.greenback.entity.Architecture;

import java.util.List;

public interface ProjetService {
    ProjetDto creerProjet(ProjetCreationDto demande);
    List<ProjetDto> listerMesProjets();
    ProjetDto obtenirProjet(Integer idProjet);
    ArchitectureDto ajouterEsquisse(Integer idProjet, MultipartFile fichier);
    List<ArchitectureDto> listerEsquisses(Integer idProjet);
    Architecture chargerEsquisse(Integer idProjet, Integer idArchitecture);
}
