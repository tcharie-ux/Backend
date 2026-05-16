package tg.ipnet.greenback.dto;

import tg.ipnet.greenback.enums.UniteMateriau;

public class MateriauxDto {
private Integer id;
private String nomMateriau;
private String type;
private float prixUnitaire;
private int quantiterTotal;
private UniteMateriau Uniter;
public Integer getId() {
    return id;
}
public void setId(Integer id) {
    this.id = id;
}
public String getNomMateriau() {
    return nomMateriau;
}
public void setNomMateriau(String nomMateriau) {
    this.nomMateriau = nomMateriau;
}
public String getType() {
    return type;
}
public void setType(String type) {
    this.type = type;
}
public float getPrixUnitaire() {
    return prixUnitaire;
}
public void setPrixUnitaire(float prixUnitaire) {
    this.prixUnitaire = prixUnitaire;
}
public int getQuantiterTotal() {
    return quantiterTotal;
}
public void setQuantiterTotal(int quantiterTotal) {
    this.quantiterTotal = quantiterTotal;
}
public UniteMateriau getUniter() {
    return Uniter;
}
public void setUniter(UniteMateriau uniter) {
    Uniter = uniter;
}
}
