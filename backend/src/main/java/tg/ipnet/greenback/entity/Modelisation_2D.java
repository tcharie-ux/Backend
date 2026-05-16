package tg.ipnet.greenback.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="modelisation_2D")
@jakarta.persistence.EntityListeners(AuditingEntityListener.class)
public class Modelisation_2D {
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)
private int id;
private String nomModele;
private LocalDateTime dateCeation;
@Column(columnDefinition = "TEXT")
private String objet;
@ManyToOne
private Projet projet;
@OneToMany(mappedBy = "modelisation2D")
private List<Estimation> estimations;

@OneToOne(mappedBy = "modelisation2D", cascade = CascadeType.ALL)
private Modelisation_3D modelisation3D;

@OneToMany(mappedBy = "modelisation2D", cascade = CascadeType.ALL)
private List<ElementPlan> elements;

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getNomModele() {
    return nomModele;
}

public void setNomModele(String nomModele) {
    this.nomModele = nomModele;
}

public LocalDateTime getDateCeation() {
    return dateCeation;
}

public void setDateCeation(LocalDateTime dateCeation) {
    this.dateCeation = dateCeation;
}

public String getObjet() {
    return objet;
}

public void setObjet(String objet) {
    this.objet = objet;
}

public Projet getProjet() {
    return projet;
}

public void setProjet(Projet projet) {
    this.projet = projet;
}

public List<Estimation> getEstimations() {
    return estimations;
}

public void setEstimations(List<Estimation> estimations) {
    this.estimations = estimations;
}

public Modelisation_3D getModelisation3D() {
    return modelisation3D;
}

public void setModelisation3D(Modelisation_3D modelisation3d) {
    modelisation3D = modelisation3d;
}

public List<ElementPlan> getElements() {
    return elements;
}

public void setElements(List<ElementPlan> elements) {
    this.elements = elements;
}
}
