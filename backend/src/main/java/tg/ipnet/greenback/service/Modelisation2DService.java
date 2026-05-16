package tg.ipnet.greenback.service;

import tg.ipnet.greenback.dto.Modelisation2DCreationDto;
import tg.ipnet.greenback.dto.Modelisation_2DDto;

import java.util.List;

public interface Modelisation2DService {
    Modelisation_2DDto creerModelisation2D(Integer idProjet, Modelisation2DCreationDto demande);
    List<Modelisation_2DDto> listerModelisations2D(Integer idProjet);
    Modelisation_2DDto obtenirModelisation2D(Integer idProjet, Integer idModelisation2D);
}
