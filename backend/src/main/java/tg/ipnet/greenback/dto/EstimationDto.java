package tg.ipnet.greenback.dto;

import java.time.LocalDateTime;
import java.util.List;

public class EstimationDto {
private Integer id;
private float couts;
private LocalDateTime dateEstimation;
private List<LigneEstimationDto> lignes;
private Integer idModelisation2D;
private Modelisation_2DDto modelisation2D;
public Integer getId() {
    return id;
}
public void setId(Integer id) {
    this.id = id;
}
public float getCouts() {
    return couts;
}
public void setCouts(float couts) {
    this.couts = couts;
}
public LocalDateTime getDateEstimation() {
    return dateEstimation;
}
public void setDateEstimation(LocalDateTime dateEstimation) {
    this.dateEstimation = dateEstimation;
}
public List<LigneEstimationDto> getLignes() {
    return lignes;
}
public void setLignes(List<LigneEstimationDto> lignes) {
    this.lignes = lignes;
}
public Integer getIdModelisation2D() {
    return idModelisation2D;
}
public void setIdModelisation2D(Integer idModelisation2D) {
    this.idModelisation2D = idModelisation2D;
}
public Modelisation_2DDto getModelisation2D() {
    return modelisation2D;
}
public void setModelisation2D(Modelisation_2DDto modelisation2d) {
    modelisation2D = modelisation2d;
}
}
