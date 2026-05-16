package tg.ipnet.greenback.dto;

import java.util.List;

public class LigneEstimationDto {
private Integer id;
private int quantter;
private float prixTotal;
private List<LigneEstimationDto> lignes;
private MateriauxDto materiau;
public Integer getId() {
    return id;
}
public void setId(Integer id) {
    this.id = id;
}
public int getQuantter() {
    return quantter;
}
public void setQuantter(int quantter) {
    this.quantter = quantter;
}
public float getPrixTotal() {
    return prixTotal;
}
public void setPrixTotal(float prixTotal) {
    this.prixTotal = prixTotal;
}
public List<LigneEstimationDto> getLignes() {
    return lignes;
}
public void setLignes(List<LigneEstimationDto> lignes) {
    this.lignes = lignes;
}
public MateriauxDto getMateriau() {
    return materiau;
}
public void setMateriau(MateriauxDto materiau) {
    this.materiau = materiau;
}
}
