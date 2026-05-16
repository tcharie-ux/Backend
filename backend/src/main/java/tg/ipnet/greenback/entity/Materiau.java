package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="materiau")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Materiau {
    @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;

private String nomMateriau;
private String type;
private float prixUnitaire;
private int quantiterTotal;
public int getId() {
    return id;
}
public void setId(int id) {
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
}
