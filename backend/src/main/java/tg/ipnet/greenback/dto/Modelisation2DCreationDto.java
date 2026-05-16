package tg.ipnet.greenback.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

public class Modelisation2DCreationDto {

    @NotBlank
    private String nomModele;

    @NotBlank
    private String objet;

    @Valid
    private List<ElementPlanCreationDto> elements = new ArrayList<>();

    public String getNomModele() {
        return nomModele;
    }

    public void setNomModele(String nomModele) {
        this.nomModele = nomModele;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public List<ElementPlanCreationDto> getElements() {
        return elements;
    }

    public void setElements(List<ElementPlanCreationDto> elements) {
        this.elements = elements;
    }
}
