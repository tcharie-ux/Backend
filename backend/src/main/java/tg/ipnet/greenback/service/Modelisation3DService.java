package tg.ipnet.greenback.service;

import tg.ipnet.greenback.dto.Modelisation3DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_3DDto;

public interface Modelisation3DService {
    Modelisation_3DDto sauvegarderModelisation3D(Integer idProjet, Integer idModelisation2D, Modelisation3DCreationDto demande);
    Modelisation_3DDto obtenirModelisation3D(Integer idProjet, Integer idModelisation2D);
}
