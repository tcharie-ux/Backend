package tg.ipnet.greenback.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="ligneEstimation")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class LigneEstimation {
 @Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
    private int quantter;
private float prixTotal;
@ManyToOne
private Estimation estimation;
public int getId() {
    return id;
}
public void setId(int id) {
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
public Estimation getEstimation() {
    return estimation;
}
public void setEstimation(Estimation estimation) {
    this.estimation = estimation;
}

}
