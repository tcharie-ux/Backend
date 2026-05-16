package tg.ipnet.greenback.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
@Entity
@Table(name="estimation")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Estimation {
 @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private float couts;
private LocalDateTime dateEstimation;
@ManyToOne
private Modelisation_2D modelisation2D;

@OneToMany(mappedBy = "estimation", cascade = CascadeType.ALL)
private List<LigneEstimation> lignes;

public int getId() {
    return id;
}

public void setId(int id) {
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

public Modelisation_2D getModelisation2D() {
    return modelisation2D;
}

public void setModelisation2D(Modelisation_2D modelisation2d) {
    modelisation2D = modelisation2d;
}

public List<LigneEstimation> getLignes() {
    return lignes;
}

public void setLignes(List<LigneEstimation> lignes) {
    this.lignes = lignes;
}
}
